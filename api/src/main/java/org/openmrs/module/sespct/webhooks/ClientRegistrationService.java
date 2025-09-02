package org.openmrs.module.sespct.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openmrs.module.sespct.config.CTConfig;
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
public class ClientRegistrationService {
	
	@Autowired
	private RestTemplate rest;
	
	@Autowired
	private OAuthService oauth;
	
	@Autowired
	private CTConfig cfg;
	
	private static final String E_CLIENTS = "/api/v1/clients";
	
	private static final OAEPParameterSpec OAEP_SHA256_SHA256 = new OAEPParameterSpec("SHA-256", "MGF1",
	        MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	/** Registers the OpenMRS client in CT (compact envelope + base64 signature). */
	public ClientRegistrationResult registerClient() throws Exception {
		// 1) Build clear payload
		ObjectNode payload = mapper.createObjectNode();
		payload.put("clientKeyId", cfg.getClientKeyId());
		payload.put("clientPublicKeyPem", cfg.getOmrsPublicPem());
		payload.put("timestamp", Instant.now().toString());
		payload.put("nonce", UUID.randomUUID().toString());
		
		// 2) Encrypt + sign (CT requires: data is Base64, signature over Base64)
		PublicKey ctPublic = CtCompactCrypto.readPublicKeyPem(cfg.getCtPublicPem());
		PrivateKey myPriv = CtCompactCrypto.readPrivateKeyPem(cfg.getOmrsPrivatePem());
		String dataB64 = compactEncryptToBase64(mapper.writeValueAsBytes(payload), ctPublic);
		String signatureB64 = signBase64(dataB64, myPriv);
		
		// 3) POST to CT (assume protected by OAuth)
		ObjectNode body = mapper.createObjectNode();
		body.put("kid", cfg.getClientKeyId());
		body.put("data", dataB64);
		body.put("signature", signatureB64);
		
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.APPLICATION_JSON);
		h.set("Authorization", "Bearer " + oauth.getToken());
		
		ResponseEntity<String> r = rest.postForEntity(cfg.getCtBaseUrl() + E_CLIENTS, new HttpEntity<String>(
		        body.toString(), h), String.class);
		if (!r.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException("register client " + r.getStatusCode());
		}
		
		// 4) Verify + decrypt CT response
		ObjectNode resp = (ObjectNode) mapper.readTree(r.getBody());
		String respDataB64 = resp.path("data").asText();
		String respSigB64 = resp.path("signature").asText();
		
		boolean ok = CtCompactCrypto.verifySignatureBase64(respDataB64, respSigB64, ctPublic);
		if (!ok)
			throw new SecurityException("CT signature invalid");
		
		byte[] clear = CtCompactCrypto.decryptCompact(respDataB64, myPriv);
		ObjectNode json = (ObjectNode) mapper.readTree(clear);
		
		ClientRegistrationResult out = new ClientRegistrationResult();
		out.setClientId(json.path("clientId").asText(null));
		out.setClientSecret(json.path("clientSecret").asText(null)); // may be null if CT doesn’t return it
		return out;
	}
	
	// ---------- helpers (same as in WebhookSubscriptionService) ----------
	
	private static String compactEncryptToBase64(byte[] clear, PublicKey ctPublic) throws Exception {
		byte[] aes = new byte[32];
		byte[] iv = new byte[12];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(aes);
		sr.nextBytes(iv);
		
		Cipher gcm = Cipher.getInstance("AES/GCM/NoPadding");
		gcm.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aes, "AES"), new GCMParameterSpec(128, iv));
		byte[] ct = gcm.doFinal(clear);
		
		Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		rsa.init(Cipher.ENCRYPT_MODE, ctPublic, OAEP_SHA256_SHA256);
		byte[] wrapped = rsa.doFinal(aes);
		
		byte[] blob = new byte[wrapped.length + iv.length + ct.length];
		System.arraycopy(wrapped, 0, blob, 0, wrapped.length);
		System.arraycopy(iv, 0, blob, wrapped.length, iv.length);
		System.arraycopy(ct, 0, blob, wrapped.length + iv.length, ct.length);
		return Base64.getEncoder().encodeToString(blob);
	}
	
	private static String signBase64(String dataB64, PrivateKey myPrivate) throws Exception {
		Signature s = Signature.getInstance("SHA256withRSA");
		s.initSign(myPrivate);
		s.update(dataB64.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(s.sign());
	}
	
	// ---------- DTO ----------
	public static class ClientRegistrationResult {
		
		private String clientId;
		
		private String clientSecret;
		
		public String getClientId() {
			return clientId;
		}
		
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}
		
		public String getClientSecret() {
			return clientSecret;
		}
		
		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
	}
}
