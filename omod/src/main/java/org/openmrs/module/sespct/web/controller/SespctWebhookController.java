package org.openmrs.module.sespct.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.config.SESPCTConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/public/webhook")
public class SespctWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SespctWebhookController.class);

    /**
     * DTO do envelope recebido (mesma estrutura que enviamos ao CT):
     * {
     *   "kid": "openmrs-key-1",
     *   "data": { "alg":"RSA-OAEP","enc":"A256GCM","iv":"...","key":"...","ciphertext":"...","tag":"..." },
     *   "signature": "base64..."
     * }
     */
    public static class EncryptedEvent {
        private String kid;
        private Map<String, Object> data;
        private String signature;

        public String getKid() { return kid; }
        public void setKid(String kid) { this.kid = kid; }

        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }

        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
    }

    @Autowired private PedidoService pedidoService;
    @Autowired private SESPCTConfig cfg;

    @RequestMapping(
            value = "/e-ft",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handle(@RequestBody EncryptedEvent enc) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1) Verificar assinatura do CT sobre os bytes do campo "data"
            byte[] dataBytes = mapper.writeValueAsBytes(enc.getData());
            boolean ok = CryptoUtils.verify(
                    dataBytes,
                    enc.getSignature(),
                    CryptoUtils.readPublicKeyPem(cfg.getCtPublicPem())
            );
            if (!ok) {
                log.warn("Webhook e-FT: assinatura inválida");
                return new ResponseEntity<Map<String, Object>>(
                        Collections.<String, Object>singletonMap("error", "invalid_signature"),
                        HttpStatus.UNAUTHORIZED
                );
            }

            // 2) Decriptar com PRIVATE do OpenMRS
            byte[] clear = CryptoUtils.decryptEnvelope(
                    enc.getData(),
                    CryptoUtils.readPrivateKeyPem(cfg.getOmrsPrivatePem())
            );
            JsonNode json = mapper.readTree(clear);

            String event     = json.path("event").asText(null);
            String requestId = json.path("requestId").asText(null);
            String facility  = json.path("facilityCode").asText(null);

            // 3) ACK rápido e processa em background (idempotente no serviço/DAO)
            if (requestId != null &&
                    ("CT_REQUEST_CREATED".equalsIgnoreCase(event) ||
                            "CT_REQUEST_UPDATED".equalsIgnoreCase(event))) {

                pedidoService.fetchAndUpsertFromCtAsync(requestId, facility);
            } else {
                log.debug("Webhook e-FT ignorado: event={}, requestId={}", event, requestId);
            }

            return new ResponseEntity<Map<String, Object>>(
                    Collections.<String, Object>singletonMap("status", "ack"),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Erro a processar webhook e-FT", e);
            return new ResponseEntity<Map<String, Object>>(
                    Collections.<String, Object>singletonMap("error", "bad_request"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
