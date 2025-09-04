package org.openmrs.module.sespct.oauth;

import org.openmrs.module.sespct.config.CTConfig;
import org.openmrs.module.sespct.crypto.CtCompactCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class OAuthService {
	
	@Autowired
	private RestTemplate rest;
	
	@Autowired
	private CTConfig cfg;
	
	private volatile String token;
	
	private volatile String refreshToken;
	
	private volatile long expEpoch;
	
	public synchronized String getToken() {
		long now = System.currentTimeMillis() / 1000;
		if (token != null && now < expEpoch - 30)
			return token;
		
		// 1) tenta refresh se existir
		if (refreshToken != null) {
			try {
				refreshWithRefreshToken();
				return token;
			}
			catch (Exception ignore) {
				// cai para client_credentials
			}
		}
		
		// 2) senão, pede novo com client_credentials
		obtainWithClientCredentials();
		return token;
	}
	
	private void obtainWithClientCredentials() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "read write");

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = cfg.getOauthClientId() + ":" + CtCompactCrypto.decryptFromGP(cfg.getOauthClientSecret());
        String basic = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        h.set("Authorization", basic);

        ResponseEntity<Map> r = rest.postForEntity(cfg.getTokenUrl(), new HttpEntity<>(form, h), Map.class);
        if (!r.getStatusCode().is2xxSuccessful()) throw new IllegalStateException("OAuth error: " + r.getStatusCode());

        Map body = r.getBody();
        token = (String) body.get("access_token");
        Number exp = (Number) body.get("expires_in");
        expEpoch = (System.currentTimeMillis() / 1000) + (exp != null ? exp.longValue() : 3600L);

        // refresh_token pode não vir no client_credentials; usa se vier
        String rt = (String) body.get("refresh_token");
        if (rt != null && !rt.isEmpty()) {
            refreshToken = rt;
            // opcional: persistir encriptado para sobreviver a restart/cluster
            // Context.getAdministrationService().setGlobalProperty(
            //     "sesp.ct.oauth.refreshToken", CtCompactCrypto.encryptForGP(rt));
        }
    }
	
	private void refreshWithRefreshToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = cfg.getOauthClientId() + ":" + CtCompactCrypto.decryptFromGP(cfg.getOauthClientSecret());
        String basic = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        h.set("Authorization", basic);

        ResponseEntity<Map> r = rest.postForEntity(cfg.getTokenUrl(), new HttpEntity<>(form, h), Map.class);
        if (!r.getStatusCode().is2xxSuccessful()) throw new IllegalStateException("OAuth refresh error: " + r.getStatusCode());

        Map body = r.getBody();
        token = (String) body.get("access_token");
        Number exp = (Number) body.get("expires_in");
        expEpoch = (System.currentTimeMillis() / 1000) + (exp != null ? exp.longValue() : 3600L);

        String rt = (String) body.get("refresh_token");
        if (rt != null && !rt.isEmpty()) refreshToken = rt; // alguns servidores rotacionam
    }
}
