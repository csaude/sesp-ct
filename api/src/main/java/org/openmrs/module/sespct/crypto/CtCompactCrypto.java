package org.openmrs.module.sespct.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
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
}
