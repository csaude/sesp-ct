package org.openmrs.module.sespct.api.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtil {
	
	private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
	
	private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	
	private static final int AES_KEY_SIZE = 256;
	
	private static final int GCM_IV_LENGTH = 12; // 96 bits
	
	private static final int GCM_TAG_LENGTH = 128; // 16 bytes
	
	private static final int RSA_KEY_LENGTH_BYTES = 256; // 2048 bits / 8
	
	/**
	 * A simple container for the final encrypted payload and its signature.
	 */
	public static class SignedRequest {
		
		public final String data;
		
		public final String signature;
		
		public SignedRequest(String data, String signature) {
			this.data = data;
			this.signature = signature;
		}
	}
	
	/**
	 * Cleans a PEM-formatted key string by removing headers, footers, and all whitespace.
	 * 
	 * @param pemKey The raw key string.
	 * @return A clean, pure Base64 string.
	 */
	private static String cleanPemKey(String pemKey) {
		return pemKey.replaceAll("-----(BEGIN|END) (PRIVATE|PUBLIC) KEY-----", "").replaceAll("\\s", "");
	}
	
	/**
	 * Loads a Private Key from a Base64 encoded PEM string.
	 */
	public static PrivateKey loadPrivateKey(String pemKey) throws Exception {
		String privateKeyPEM = cleanPemKey(pemKey);
		byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		return keyFactory.generatePrivate(keySpec);
	}
	
	/**
	 * Loads a Public Key from a Base64 encoded PEM string.
	 */
	public static PublicKey loadPublicKey(String pemKey) throws Exception {
		String publicKeyPEM = cleanPemKey(pemKey);
		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
		return keyFactory.generatePublic(keySpec);
	}
	
	/**
	 * Encrypts and signs a JSON payload to be sent to the API. Follows the hybrid encryption
	 * scheme.
	 * 
	 * @param jsonPayload The raw JSON string to send.
	 * @param serverPublicKey The server's public key for encrypting the AES key.
	 * @param clientPrivateKey Your private key for signing the final payload.
	 * @return A SignedRequest object containing the Base64 encoded data and signature.
	 */
	public static SignedRequest encryptAndSign(String jsonPayload, PublicKey serverPublicKey, PrivateKey clientPrivateKey)
	        throws Exception {
		// 1. Generate a random AES-256 key
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(AES_KEY_SIZE);
		SecretKey aesKey = keyGen.generateKey();
		
		// 2. Generate a random IV for AES/GCM
		byte[] iv = new byte[GCM_IV_LENGTH];
		new SecureRandom().nextBytes(iv);
		
		// 3. Encrypt the JSON data using AES/GCM
		Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec);
		byte[] aesCiphertext = aesCipher.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));
		
		// 4. Encrypt the AES key using the server's public RSA key
		Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION);
		rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
		byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
		
		// 5. Build the final payload: encryptedAesKey + iv + aesCiphertext
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(encryptedAesKey);
		outputStream.write(iv);
		outputStream.write(aesCiphertext);
		byte[] combinedPayload = outputStream.toByteArray();
		
		String encryptedPayloadB64 = Base64.getEncoder().encodeToString(combinedPayload);
		
		// 6. Sign the Base64 encoded payload with your private key
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(clientPrivateKey);
		signature.update(encryptedPayloadB64.getBytes(StandardCharsets.UTF_8));
		byte[] signatureBytes = signature.sign();
		
		String signatureB64 = Base64.getEncoder().encodeToString(signatureBytes);
		
		// 7. Return the final request data
		return new SignedRequest(encryptedPayloadB64, signatureB64);
	}
	
	/**
	 * Decrypts and verifies a response from the API.
	 */
	public static String decryptAndVerify(String encryptedDataB64, String signatureB64, PrivateKey clientPrivateKey,
	        PublicKey serverPublicKey) throws Exception {
		// 1. Verify the signature first
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(serverPublicKey);
		signature.update(encryptedDataB64.getBytes(StandardCharsets.UTF_8));
		if (!signature.verify(Base64.getDecoder().decode(signatureB64))) {
			throw new GeneralSecurityException("Signature verification failed.");
		}
		
		// 2. Decode the main payload
		byte[] combined = Base64.getDecoder().decode(encryptedDataB64);
		
		// 3. Deconstruct the payload
		byte[] encryptedAesKey = new byte[RSA_KEY_LENGTH_BYTES];
		byte[] iv = new byte[GCM_IV_LENGTH];
		byte[] aesCiphertext = new byte[combined.length - RSA_KEY_LENGTH_BYTES - GCM_IV_LENGTH];
		
		System.arraycopy(combined, 0, encryptedAesKey, 0, RSA_KEY_LENGTH_BYTES);
		System.arraycopy(combined, RSA_KEY_LENGTH_BYTES, iv, 0, GCM_IV_LENGTH);
		System.arraycopy(combined, RSA_KEY_LENGTH_BYTES + GCM_IV_LENGTH, aesCiphertext, 0, aesCiphertext.length);
		
		// 4. Decrypt the AES key
		Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION);
		rsaCipher.init(Cipher.DECRYPT_MODE, clientPrivateKey);
		SecretKey aesKey = new SecretKeySpec(rsaCipher.doFinal(encryptedAesKey), "AES");
		
		// 5. Decrypt the data
		Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmParameterSpec);
		
		byte[] decryptedData = aesCipher.doFinal(aesCiphertext);
		return new String(decryptedData, StandardCharsets.UTF_8);
	}
}
