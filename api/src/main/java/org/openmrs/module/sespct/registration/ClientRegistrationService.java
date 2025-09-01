package org.openmrs.module.sespct.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openmrs.module.sespct.config.SESPCTConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClientRegistrationService {

    @Autowired private RestTemplate rest;
    @Autowired private SESPCTConfig cfg;

    public void registerClient() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();
            body.put("clientName", "OpenMRS-CT");
            body.put("publicKeyPem", cfg.getOmrsPublicPem());

            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> r = rest.postForEntity(
                    cfg.getCtBaseUrl() + "/api/clients",
                    new HttpEntity<String>(body.toString(), h),
                    String.class
            );
            if (!r.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Client register " + r.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Register client error", e);
        }
    }
}
