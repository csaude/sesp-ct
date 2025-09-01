package org.openmrs.module.sespct.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openmrs.module.sespct.config.SESPCTConfig;
import org.openmrs.module.sespct.crypto.CtCompactCrypto;
import org.openmrs.module.sespct.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class WebhookSubscriptionService {

    @Autowired private RestTemplate rest;
    @Autowired private OAuthService oauth;
    @Autowired private SESPCTConfig cfg;

    private static final OAEPParameterSpec OAEP_SHA256_SHA256 =
            new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    private final ObjectMapper mapper = new ObjectMapper();

    /** Create (register) a webhook in CT using compact envelope + base64 signature. */
    public String createSubscription() throws Exception {
        // 1) Build clear payload
        ObjectNode payload = mapper.createObjectNode();
        payload.put("url", cfg.getWebhookUrl());
        payload.putArray("events").add("CT_REQUEST_CREATED").add("CT_REQUEST_UPDATED");
        payload.put("clientKeyId", cfg.getClientKeyId());
        payload.put("clientPublicKeyPem", cfg.getOmrsPublicPem());
        payload.put("timestamp", Instant.now().toString());
        payload.put("nonce", UUID.randomUUID().toString());

        // 2) Encrypt (compact) with CT public key and sign the BASE64 with our private key
        PublicKey ctPublic  = CtCompactCrypto.readPublicKeyPem(cfg.getCtPublicPem());
        PrivateKey myPriv   = CtCompactCrypto.readPrivateKeyPem(cfg.getOmrsPrivatePem());
        String dataB64      = compactEncryptToBase64(mapper.writeValueAsBytes(payload), ctPublic);
        String signatureB64 = signBase64(dataB64, myPriv);

        // 3) POST to CT
        ObjectNode body = mapper.createObjectNode();
        body.put("kid", cfg.getClientKeyId());
        body.put("data", dataB64);          // base64 string
        body.put("signature", signatureB64);

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Authorization", "Bearer " + oauth.getToken()); // Spring 4.x

        ResponseEntity<String> r = rest.postForEntity(
                cfg.getCtBaseUrl() + "/api/v1/webhooks",
                new HttpEntity<String>(body.toString(), h),
                String.class
        );
        if (!r.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("subscribe " + r.getStatusCode());
        }

        // 4) Verify + decrypt CT response (also compact format)
        ObjectNode resp = (ObjectNode) mapper.readTree(r.getBody());
        String respDataB64 = resp.path("data").asText();
        String respSigB64  = resp.path("signature").asText();

        // CT signs the BASE64 string
        boolean ok = CtCompactCrypto.verifySignatureBase64(respDataB64, respSigB64, ctPublic);
        if (!ok) throw new SecurityException("CT signature invalid");

        byte[] clear = CtCompactCrypto.decryptCompact(respDataB64, myPriv);
        ObjectNode json = (ObjectNode) mapper.readTree(clear);
        return json.get("subscriptionId").asText();
    }

    /** Delete webhook in CT (same compact envelope/signing). */
    public void deleteSubscription(String subscriptionId) throws Exception {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("subscriptionId", subscriptionId);
        payload.put("timestamp", Instant.now().toString());

        PublicKey ctPublic  = CtCompactCrypto.readPublicKeyPem(cfg.getCtPublicPem());
        PrivateKey myPriv   = CtCompactCrypto.readPrivateKeyPem(cfg.getOmrsPrivatePem());
        String dataB64      = compactEncryptToBase64(mapper.writeValueAsBytes(payload), ctPublic);
        String signatureB64 = signBase64(dataB64, myPriv);

        ObjectNode body = mapper.createObjectNode();
        body.put("kid", cfg.getClientKeyId());
        body.put("data", dataB64);
        body.put("signature", signatureB64);

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Authorization", "Bearer " + oauth.getToken());

        rest.exchange(
                cfg.getCtBaseUrl() + "/api/v1/webhooks",
                org.springframework.http.HttpMethod.DELETE,
                new HttpEntity<String>(body.toString(), h),
                String.class
        );
    }

    /* ========== Compact envelope helpers (exactly what we validated) ========== */

    /** Encrypts clear bytes using AES-GCM (IV=12) and wraps the AES key with RSA-OAEP(SHA-256/MGF1-SHA-256), returns Base64(RSA||IV||CT). */
    private static String compactEncryptToBase64(byte[] clear, PublicKey ctPublic) throws Exception {
        // AES-256 key + 12-byte IV
        byte[] aes = new byte[32];
        byte[] iv  = new byte[12];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(aes);
        sr.nextBytes(iv);

        // AES-GCM encrypt
        Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
        gcm.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aes, "AES"), new GCMParameterSpec(128, iv));
        byte[] ct = gcm.doFinal(clear);

        // RSA-OAEP wrap AES key with CT public key
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, ctPublic, OAEP_SHA256_SHA256);
        byte[] wrapped = rsa.doFinal(aes);

        // Assemble: RSA || IV || CT
        byte[] blob = new byte[wrapped.length + iv.length + ct.length];
        System.arraycopy(wrapped, 0, blob, 0, wrapped.length);
        System.arraycopy(iv,      0, blob, wrapped.length, iv.length);
        System.arraycopy(ct,      0, blob, wrapped.length + iv.length, ct.length);

        return Base64.getEncoder().encodeToString(blob);
    }

    /** Sign the BASE64 string (CT checks signature over base64). */
    private static String signBase64(String dataB64, PrivateKey myPrivate) throws Exception {
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(myPrivate);
        s.update(dataB64.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(s.sign());
    }
}
