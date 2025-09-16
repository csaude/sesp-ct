package org.openmrs.module.sespct.api.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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

/**
 * Utility class for handling encryption, decryption, and signing for the SESP-CT Middleware API.
 * Adapted for Java 8 from the provided CtCompactCrypto class.
 * This is a stateless utility class with static methods.
 */
public final class MiddlewareCryptoUtil {

    // Private constructor to prevent instantiation
    private MiddlewareCryptoUtil() {
    }

    private static final OAEPParameterSpec OAEP_SHA256_SHA256 = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
    private static final int GCM_TAG_BITS = 128; // 16 bytes tag
    private static final int GCM_IV_BYTES = 12;  // 12 bytes IV (nonce)
    private static final int AES_BITS = 256;

    /* ===================== PEM UTILS ===================== */

    /**
     * Reads a private key in PEM format (PKCS#8 required).
     */
    public static PrivateKey readPrivateKeyPem(String pem) throws Exception {
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            throw new IllegalArgumentException("PKCS#1 format detected. Please convert to PKCS#8: openssl pkcs8 -topk8 -in key.pem -out key_pkcs8.pem -nocrypt");
        }
        String b64 = pem.replaceAll("-----BEGIN [A-Z0-9 ]+-----", "")
                .replaceAll("-----END [A-Z0-9 ]+-----", "")
                .replaceAll("\\s", ""); // More robust whitespace removal
        byte[] der = Base64.getDecoder().decode(b64);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    /**
     * Reads a public key in PEM format (X.509).
     */
    public static PublicKey readPublicKeyPem(String pem) throws Exception {
        String b64 = pem.replaceAll("-----BEGIN [A-Z0-9 ]+-----", "")
                .replaceAll("-----END [A-Z0-9 ]+-----", "")
                .replaceAll("\\s", ""); // More robust whitespace removal
        byte[] der = Base64.getDecoder().decode(b64);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
    }

    /* ===================== COMPACT ENVELOPE (ENCRYPT/DECRYPT) ===================== */

    /**
     * Encrypts a JSON UTF-8 string using AES-GCM and wraps the key with RSA-OAEP(SHA-256).
     *
     * @return Base64 encoded encrypted blob.
     */
    public static String encryptCompact(String jsonUtf8, PublicKey serverPublicKey) throws Exception {
        // 1. Generate a new AES-256 key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(AES_BITS, new SecureRandom());
        SecretKey aesKey = kg.generateKey();

        // 2. Encrypt the data with AES-GCM
        byte[] iv = new byte[GCM_IV_BYTES];
        new SecureRandom().nextBytes(iv);
        Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
        gcm.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] ciphertext = gcm.doFinal(jsonUtf8.getBytes(StandardCharsets.UTF_8));

        // 3. Wrap the AES key with RSA-OAEP using the server's public key
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, serverPublicKey, OAEP_SHA256_SHA256);
        byte[] wrappedKey = rsa.doFinal(aesKey.getEncoded());

        // 4. Concatenate: blob = wrappedKey || iv || ciphertext
        byte[] blob = new byte[wrappedKey.length + iv.length + ciphertext.length];
        System.arraycopy(wrappedKey, 0, blob, 0, wrappedKey.length);
        System.arraycopy(iv, 0, blob, wrappedKey.length, iv.length);
        System.arraycopy(ciphertext, 0, blob, wrappedKey.length + iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(blob);
    }

    /**
     * Decrypts a Base64 encoded blob: Base64(RSA(wrappedKey) || IV || AES-GCM(ciphertext+tag)).
     *
     * @return The decrypted data as a String.
     */
    public static String decryptCompact(String dataB64, PrivateKey clientPrivateKey) throws Exception {
        byte[] blob = Base64.getDecoder().decode(dataB64);

        int rsaLen = (((RSAPrivateKey) clientPrivateKey).getModulus().bitLength() + 7) / 8; // e.g., 2048 bits -> 256 bytes
        if (blob.length < rsaLen + GCM_IV_BYTES + (GCM_TAG_BITS / 8)) {
            throw new IllegalArgumentException("Encrypted blob is too small.");
        }

        // 1. Split the blob into its components
        byte[] wrappedKey = slice(blob, 0, rsaLen);
        byte[] iv = slice(blob, rsaLen, rsaLen + GCM_IV_BYTES);
        byte[] ciphertext = slice(blob, rsaLen + GCM_IV_BYTES, blob.length);

        // 2. Unwrap the AES key with RSA-OAEP using our private key
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.DECRYPT_MODE, clientPrivateKey, OAEP_SHA256_SHA256);
        byte[] aesKeyBytes = rsa.doFinal(wrappedKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 3. Decrypt the data with AES-GCM
        Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
        gcm.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] plaintext = gcm.doFinal(ciphertext);

        return new String(plaintext, StandardCharsets.UTF_8);
    }

    /* ===================== SIGN/VERIFY ===================== */

    /**
     * Signs the Base64 STRING (not the decoded bytes) with SHA256withRSA.
     *
     * @return Base64 encoded signature.
     */
    public static String signBase64String(String dataB64, PrivateKey privateKey) throws Exception {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(privateKey);
        s.update(dataB64.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(s.sign());
    }

    /**
     * Verifies a signature made over the Base64 STRING.
     */
    public static boolean verifySignatureOverString(String dataB64, String signatureB64, PublicKey publicKey) throws Exception {
        byte[] sigBytes = Base64.getDecoder().decode(signatureB64);
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(publicKey);
        s.update(dataB64.getBytes(StandardCharsets.UTF_8));
        return s.verify(sigBytes);
    }

    /* ===================== HELPERS ===================== */

    private static byte[] slice(byte[] array, int from, int to) {
        int len = to - from;
        byte[] out = new byte[len];
        System.arraycopy(array, from, out, 0, len);
        return out;
    }
}