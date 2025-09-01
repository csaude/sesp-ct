package org.openmrs.module.sespct.ct;

import com.fasterxml.jackson.databind.JsonNode;
import org.openmrs.module.sespct.config.SESPCTConfig;
import org.openmrs.module.sespct.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class CtClientImpl implements CtClient {

    private final RestTemplate rest;
    private final OAuthService oauth;
    private final SESPCTConfig cfg;

    @Autowired
    public CtClientImpl(RestTemplate rest, OAuthService oauth, SESPCTConfig cfg) {
        this.rest = rest;
        this.oauth = oauth;
        this.cfg = cfg;
    }

    @Override
    public JsonNode getPedidoById(String id, String facility) {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "Bearer " + oauth.getToken());   // Spring 4.x
        h.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<JsonNode> r = rest.exchange(
                cfg.getCtBaseUrl() + "/api/requests/{id}?facilityCode={f}",
                HttpMethod.GET,
                new HttpEntity<Void>(h),
                JsonNode.class,
                id, facility
        );
        return r.getBody();
    }

    @Override
    public JsonNode getPedidosSince(String since, String facility) {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "Bearer " + oauth.getToken());   // Spring 4.x
        h.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<JsonNode> r = rest.exchange(
                cfg.getCtBaseUrl() + "/api/requests?since={s}&facilityCode={f}",
                HttpMethod.GET,
                new HttpEntity<Void>(h),
                JsonNode.class,
                since, facility
        );
        return r.getBody();
    }
}
