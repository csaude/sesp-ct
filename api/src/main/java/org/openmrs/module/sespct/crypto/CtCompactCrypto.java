package org.openmrs.module.sespct.crypto;

import org.openmrs.api.context.Context;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class CtCompactCrypto {
	
	private static final OAEPParameterSpec OAEP_SHA256_SHA256 = new OAEPParameterSpec("SHA-256", "MGF1",
	        MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
	
	private static final String GP_MASTER_KEY = "sesp.ct.kms.masterKeyB64"; // 32 bytes base64
	
	private static final int GCM_TAG_BITS = 128; // 16 bytes tag
	
	private static final int GCM_IV_BYTES = 12;
	
	private CtCompactCrypto() {
	}
	
	public static PrivateKey readPrivateKeyPem(String pem) throws Exception {
		if (pem.contains("BEGIN RSA PRIVATE KEY")) {
			throw new IllegalArgumentException(
			        "PKCS#1 detected. Convert to PKCS#8: openssl pkcs8 -topk8 -in key.pem -out key_pkcs8.pem -nocrypt");
		}
		String b64 = pem.replaceAll("-----BEGIN [A-Z0-9 ]+-----", "").replaceAll("-----END [A-Z0-9 ]+-----", "")
		        .replaceAll("(?m)^Proc-Type:.*\\R?", "").replaceAll("(?m)^DEK-Info:.*\\R?", "")
		        .replaceAll("[^A-Za-z0-9+/=]", "");
		byte[] der = Base64.getDecoder().decode(b64);
		return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
	}
	
	public static PublicKey readPublicKeyPem(String pem) throws Exception {
		String b64 = pem.replaceAll("-----BEGIN [A-Z0-9 ]+-----", "").replaceAll("-----END [A-Z0-9 ]+-----", "")
		        .replaceAll("[^A-Za-z0-9+/=]", "");
		byte[] der = Base64.getDecoder().decode(b64);
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
	}
	
	/* ======== What CT actually does (per your test) ======== */
	
	/** Verify CT signature made over the BASE64 data string using CT public key. */
	public static boolean verifySignatureBase64(String dataB64, String signatureB64, PublicKey ctPublic) throws Exception {
		byte[] sig = Base64.getDecoder().decode(signatureB64);
		Signature s = Signature.getInstance("SHA256withRSA");
		s.initVerify(ctPublic);
		s.update(dataB64.getBytes(StandardCharsets.UTF_8));
		return s.verify(sig);
	}
	
	/** Decrypt compact envelope: data = RSA(wrappedKey) || IV(12) || AES-GCM(ciphertext+tag). */
	public static byte[] decryptCompact(String dataB64, PrivateKey clientPrivate) throws Exception {
		byte[] blob = Base64.getDecoder().decode(dataB64);
		
		int rsaLen = (((RSAPrivateKey) clientPrivate).getModulus().bitLength() + 7) / 8; // e.g., 256 for 2048-bit
		if (blob.length < rsaLen + 12 + 16) {
			throw new IllegalArgumentException("Blob too small for rsaLen=" + rsaLen + " (len=" + blob.length + ")");
		}
		
		// Split: RSA(encrypted AES key) || IV(12) || CT+TAG
		byte[] wrapped = slice(blob, 0, rsaLen);
		byte[] iv = slice(blob, rsaLen, rsaLen + 12);
		byte[] ctTag = slice(blob, rsaLen + 12, blob.length);
		
		// RSA-OAEP(SHA-256, MGF1-SHA-256)
		Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		rsa.init(Cipher.DECRYPT_MODE, clientPrivate, OAEP_SHA256_SHA256);
		byte[] aes = rsa.doFinal(wrapped);
		
		// AES-GCM with 128-bit tag
		Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
		gcm.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aes, "AES"), new GCMParameterSpec(128, iv));
		return gcm.doFinal(ctTag);
	}
	
	private static byte[] slice(byte[] a, int from, int to) {
		int len = to - from;
		byte[] out = new byte[len];
		System.arraycopy(a, from, out, 0, len);
		return out;
	}
	
	public static String encryptForGP(String plain) {
		if (plain == null || plain.trim().isEmpty())
			return "";
		try {
			SecretKey key = loadOrCreateMasterKey(); // may create & store on first run
			if (key == null)
				return "{b64}" + Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
			
			byte[] iv = new byte[GCM_IV_BYTES];
			new SecureRandom().nextBytes(iv);
			
			Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
			gcm.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
			byte[] ct = gcm.doFinal(plain.getBytes(StandardCharsets.UTF_8));
			
			// store as: v1|base64(iv|ct)
			ByteBuffer bb = ByteBuffer.allocate(iv.length + ct.length);
			bb.put(iv).put(ct);
			String payload = Base64.getEncoder().encodeToString(bb.array());
			return "{v1}" + payload;
		}
		catch (Exception e) {
			// last-resort fallback
			return "{b64}" + Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
		}
	}
	
	/** Decrypts a GP value encrypted by encryptForGP (supports {v1} and {b64} fallbacks). */
	public static String decryptFromGP(String stored) {
		if (stored == null || stored.trim().isEmpty())
			return "";
		try {
			if (stored.startsWith("{v1}")) {
				String b64 = stored.substring("{v1}".length());
				byte[] blob = Base64.getDecoder().decode(b64);
				if (blob.length < GCM_IV_BYTES + 16)
					return "";
				
				byte[] iv = slice(blob, 0, GCM_IV_BYTES);
				byte[] ct = slice(blob, GCM_IV_BYTES, blob.length);
				
				SecretKey key = loadOrCreateMasterKey();
				if (key == null)
					return ""; // cannot decrypt
					
				Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
				gcm.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
				byte[] clear = gcm.doFinal(ct);
				return new String(clear, StandardCharsets.UTF_8);
			}
			if (stored.startsWith("{b64}")) {
				String b64 = stored.substring("{b64}".length());
				byte[] bytes = Base64.getDecoder().decode(b64);
				return new String(bytes, StandardCharsets.UTF_8);
			}
			// plain (not encrypted)
			return stored;
		}
		catch (Exception e) {
			return "";
		}
	}
	
	/** Loads master key from GP or creates a new 32-byte key and saves it Base64-encoded. */
	private static SecretKey loadOrCreateMasterKey() {
        String b64 = Context.getAdministrationService().getGlobalProperty(GP_MASTER_KEY);
        try {
            if (b64 == null || b64.trim().isEmpty()) {
                // create new 256-bit key
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(256, new SecureRandom());
                SecretKey key = kg.generateKey();
                String enc = Base64.getEncoder().encodeToString(key.getEncoded());
                Context.getAdministrationService().setGlobalProperty(GP_MASTER_KEY, enc);
                return key;
            } else {
                byte[] raw = Base64.getDecoder().decode(b64.trim());
                return new SecretKeySpec(raw, "AES");
            }
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return null; // caller will fallback to {b64}
        }
    }
}
