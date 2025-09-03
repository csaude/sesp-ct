package org.openmrs.module.sespct.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.config.CTConfig;
import org.openmrs.module.sespct.crypto.CtCompactCrypto;
import org.openmrs.module.sespct.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
import java.util.HashMap;
import java.util.Map;
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
	
	public ClientRegistrationResult registerClient() {
		// Read clientId and (decrypted) clientSecret from GP
		String clientId = Context.getAdministrationService().getGlobalProperty("sesp.ct.oauth.clientId");
		String storedSecret = Context.getAdministrationService().getGlobalProperty("sesp.ct.oauth.clientSecret");
		String clientSecret = CtCompactCrypto.decryptFromGP(storedSecret); // falls back if not encrypted
		
		if (clientId == null || clientId.trim().isEmpty())
			throw new IllegalStateException("Missing GP sesp.ct.oauth.clientId");
		if (clientSecret == null || clientSecret.trim().isEmpty())
			throw new IllegalStateException("Missing GP sesp.ct.oauth.clientSecret");
		
		// Build JSON body exactly like your working curl
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("clientId", clientId);
		body.put("clientSecret", clientSecret);
		body.put("publicKey", cfg.getOmrsPublicPem()); // PEM with \n
		body.put("keyExpirationDuration", 365);
		body.put("initialKeyVersion", "1");
		body.put("scopes", "read,write"); // keep EXACT as your sample
		
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.APPLICATION_JSON);
		h.set("scopes", "admin"); // required by CT to create clients
		
		ResponseEntity<Map> r = rest
		        .exchange(cfg.getRegisterUrl(), HttpMethod.POST, new HttpEntity<Map>(body, h), Map.class);
		
		if (!r.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException("register client " + r.getStatusCode());
		}
		
		Map<?, ?> m = r.getBody();
		ClientRegistrationResult out = new ClientRegistrationResult();
		
		// CT may echo/normalize the clientId
		if (m != null && m.get("clientId") != null) {
			out.setClientId(String.valueOf(m.get("clientId")));
			// persist normalized id
			Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientId", out.getClientId());
		} else {
			out.setClientId(clientId);
		}
		
		// Save CT server public key if present and not already set
		if (m != null && m.get("serverPublicKey") != null) {
			String ctPub = String.valueOf(m.get("serverPublicKey"));
			String existing = Context.getAdministrationService().getGlobalProperty("sesp.ct.keys.ctPublicPem");
			if (existing == null || existing.trim().isEmpty()) {
				Context.getAdministrationService().setGlobalProperty("sesp.ct.keys.ctPublicPem", ctPub);
			}
		}
		
		// Keep the clientSecret you used; re-save encrypted (idempotent)
		Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientSecret",
		    CtCompactCrypto.encryptForGP(clientSecret));
		out.setClientSecret(clientSecret);
		
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
