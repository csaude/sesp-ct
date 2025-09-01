package org.openmrs.module.sespct.oauth;

import org.openmrs.module.sespct.config.SESPCTConfig;
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

    @Autowired private RestTemplate rest;
    @Autowired private SESPCTConfig cfg;

    private volatile String token;
    private volatile long expEpoch;

    public synchronized String getToken() {
        long now = System.currentTimeMillis() / 1000;
        if (token != null && now < expEpoch - 30) return token;

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = cfg.getOauthClientId() + ":" + cfg.getOauthClientSecret();
        String basic = "Basic " + Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        h.set("Authorization", basic);

        ResponseEntity<Map> r = rest.postForEntity(
                cfg.getTokenUrl(),
                new HttpEntity<MultiValueMap<String,String>>(form, h),
                Map.class
        );

        if (!r.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("OAuth error: " + r.getStatusCode());
        }

        token = (String) r.getBody().get("access_token");
        Integer exp = (Integer) r.getBody().get("expires_in");
        expEpoch = (System.currentTimeMillis()/1000) + (exp != null ? exp.intValue() : 3600);
        return token;
    }

}
