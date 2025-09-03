package org.openmrs.module.sespct.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.crypto.CtCompactCrypto;
import org.openmrs.module.sespct.oauth.OAuthService;
import org.openmrs.module.sespct.registration.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.*;

@Component("sespctCtBootstrap")
public class CtBootstrap {
	
	@Autowired
	private RestTemplate rest;
	
	@Autowired
	private OAuthService oauth;
	
	@Autowired(required = false)
	private ClientRegistrationService clientRegistrationService;
	
	public void run() {
		// 0) make sure base URLs point to the correct host/paths
		primeBaseUrls();
		
		// 1) ensure clientId
		ensureClientId();
		
		// 2) ensure RSA keypair (OpenMRS side)
		ensureKeyPair();
		
		// 3) ensure registration on CT and save encrypted secret + ct server public key
		ensureRegistration();
		
		// 4) fetch and persist token (access/refresh/expiry) using your OAuthService
		String token = oauth.getToken();
		if (token == null || token.trim().isEmpty()) {
			throw new IllegalStateException("Failed to obtain CT access token");
		}
	}
	
	private void primeBaseUrls() {
		if (isBlank(Context.getAdministrationService().getGlobalProperty("sesp.ct.api.baseUrl"))) {
			Context.getAdministrationService().setGlobalProperty("sesp.ct.api.baseUrl", "https://api.comitetarvmisau.co.mz");
		}
		if (isBlank(Context.getAdministrationService().getGlobalProperty("sesp.ct.register.url"))) {
			Context.getAdministrationService().setGlobalProperty("sesp.ct.register.url",
			    gp("sesp.ct.api.baseUrl") + "/oauth2/clients");
		}
		if (isBlank(Context.getAdministrationService().getGlobalProperty("sesp.ct.oauth.tokenUrl"))) {
			Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.tokenUrl",
			    gp("sesp.ct.api.baseUrl") + "/oauth2/token");
		}
	}
	
	private void ensureClientId() {
		String cid = gp("sesp.ct.oauth.clientId");
		if (isBlank(cid)) {
			// e.g.: sespct_4f2c9a7b_20250903
			String gen = "sespct_" + randomHex(8) + "_" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
			Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientId", gen);
		}
	}
	
	private void ensureKeyPair() {
		String pub = gp("sesp.ct.keys.omrsPublicPem");
		String prv = gp("sesp.ct.keys.omrsPrivatePem");
		if (isBlank(pub) || isBlank(prv)) {
			try {
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(2048, new SecureRandom());
				KeyPair kp = kpg.generateKeyPair();
				
				String pubPem = toPem("PUBLIC KEY", kp.getPublic().getEncoded());
				String prvPem = toPem("PRIVATE KEY", kp.getPrivate().getEncoded());
				
				Context.getAdministrationService().setGlobalProperty("sesp.ct.keys.omrsPublicPem", pubPem);
				Context.getAdministrationService().setGlobalProperty("sesp.ct.keys.omrsPrivatePem", prvPem);
			}
			catch (Exception e) {
				throw new RuntimeException("Failed to generate RSA keypair", e);
			}
		}
	}
	
	private void ensureRegistration() {
		String clientId = gp("sesp.ct.oauth.clientId");
		String encSecret = gp("sesp.ct.oauth.clientSecret");
		String ctPubPem = gp("sesp.ct.keys.ctPublicPem");
		
		if (!isBlank(encSecret)) {
			// already registered
			return;
		}
		
		// generate strong secret
		String plainSecret = "secret-" + UUID.randomUUID();
		
		if (isBlank(ctPubPem)) {
			// We don't have CT public key yet → do a plain /oauth2/clients register
			PlainRegisterResult rr = plainRegister(clientId, plainSecret, gp("sesp.ct.keys.omrsPublicPem"));
			// store CT server public key if returned
			if (!isBlank(rr.serverPublicKey)) {
				Context.getAdministrationService().setGlobalProperty("sesp.ct.keys.ctPublicPem", rr.serverPublicKey);
			}
		} else {
			// We already have CT public key – use your encrypted ClientRegistrationService
			if (clientRegistrationService == null)
				throw new IllegalStateException("ClientRegistrationService bean not found");
			try {
				ClientRegistrationService.ClientRegistrationResult res = clientRegistrationService.registerClient();
				// If CT generated/returned a secret, prefer that; else keep ours
				if (res.getClientSecret() != null && !res.getClientSecret().trim().isEmpty()) {
					plainSecret = res.getClientSecret();
				}
				// If CT returned a clientId, save it (in case it differs)
				if (res.getClientId() != null && !res.getClientId().trim().isEmpty()) {
					Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientId", res.getClientId());
				}
			}
			catch (Exception e) {
				throw new RuntimeException("Encrypted registration failed", e);
			}
		}
		
		// save client secret ENCRYPTED
		Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientSecret",
		    CtCompactCrypto.encryptForGP(plainSecret));
	}
	
	// ---------- helpers ----------
	
	private PlainRegisterResult plainRegister(String clientId, String clientSecret, String publicPem) {
		String registerUrl = gp("sesp.ct.register.url"); // e.g. https://api.../oauth2/clients
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("clientId", clientId);
		body.put("clientSecret", clientSecret);
		body.put("publicKey", publicPem);
		body.put("keyExpirationDuration", 365);
		body.put("initialKeyVersion", "1");
		body.put("scopes", "read,write");
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("scopes", "admin");
		
		ResponseEntity<Map> resp = rest
		        .exchange(registerUrl, HttpMethod.POST, new HttpEntity<Map>(body, headers), Map.class);
		
		if (!resp.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException("Client register failed: " + resp.getStatusCode());
		}
		Map<?, ?> m = resp.getBody();
		PlainRegisterResult out = new PlainRegisterResult();
		out.clientId = m != null && m.get("clientId") != null ? String.valueOf(m.get("clientId")) : clientId;
		out.serverPublicKey = m != null && m.get("serverPublicKey") != null ? String.valueOf(m.get("serverPublicKey"))
		        : null;
		
		// save clientId in case server normalized it
		Context.getAdministrationService().setGlobalProperty("sesp.ct.oauth.clientId", out.clientId);
		return out;
	}
	
	private static class PlainRegisterResult {
		
		String clientId;
		
		String serverPublicKey;
	}
	
	private String gp(String key) {
		return Context.getAdministrationService().getGlobalProperty(key);
	}
	
	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
	
	private static String toPem(String type, byte[] der) {
		String b64 = java.util.Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(der);
		return "-----BEGIN " + type + "-----\n" + b64 + "\n-----END " + type + "-----";
	}
	
	private static String randomHex(int n) {
		byte[] b = new byte[n / 2];
		new SecureRandom().nextBytes(b);
		StringBuilder sb = new StringBuilder();
		for (byte x : b)
			sb.append(String.format("%02x", x));
		return sb.toString();
	}
}
