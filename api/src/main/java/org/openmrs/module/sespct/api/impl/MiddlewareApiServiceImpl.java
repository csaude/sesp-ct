package org.openmrs.module.sespct.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.MiddlewareApiService;
import org.openmrs.module.sespct.api.dto.EncryptedRequestDTO;
import org.openmrs.module.sespct.api.dto.MarkConsumedPayload;
import org.openmrs.module.sespct.api.dto.PedidoDTO;
import org.openmrs.module.sespct.api.dto.RespostaDTO;
import org.openmrs.module.sespct.api.util.MiddlewareCryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the MiddlewareApiService.
 */
@Service
public class MiddlewareApiServiceImpl extends BaseOpenmrsService implements MiddlewareApiService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String baseUrl;
    private String usCode;
    private String clientId;
    private String clientSecret;

    private PrivateKey ourPrivateKey;
    private PublicKey serverPublicKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method is called by Spring after the bean is created.
     * It loads all necessary configuration from Global Properties.
     */
    @PostConstruct
    private void initialize() {
        log.info("Initializing MiddlewareApiService...");
        try {
            baseUrl = getGP("sespct.api.baseUrl");
            usCode = getGP("sespct.api.usCode");
            clientId = getGP("sespct.api.clientId");
            clientSecret = getGP("sespct.api.clientSecret");

            String ourPrivatePem = getGP("sespct.api.privateKey");
            String serverPublicPem = getGP("sespct.api.serverPublicKey");

            if (ourPrivatePem == null || ourPrivatePem.isEmpty()) {
                log.error("Our private key ('sespct.api.privateKey') is not configured. Service cannot function.");
                return;
            }
            ourPrivateKey = MiddlewareCryptoUtil.readPrivateKeyPem(ourPrivatePem);

            if (serverPublicPem != null && !serverPublicPem.isEmpty()) {
                serverPublicKey = MiddlewareCryptoUtil.readPublicKeyPem(serverPublicPem);
            } else {
                log.warn("Server public key ('sespct.api.serverPublicKey') is not yet configured. Some operations will fail.");
            }
            log.info("MiddlewareApiService initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize MiddlewareApiService from Global Properties", e);
        }
    }

    @Override
    public String login() {
        String url = baseUrl + "/login";
        log.info("Attempting to login at: " + url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            Map<String, String> payload = new HashMap<>();
            payload.put("username", clientId);
            payload.put("password", clientSecret);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            post.setEntity(new StringEntity(jsonPayload));

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                String token = rootNode.path("token").asText();
                log.info("Successfully logged in and received auth token.");
                return token;
            } else {
                log.error("Login failed. Status code: " + statusCode);
                return null;
            }
        } catch (Exception e) {
            log.error("Error during login to middleware API", e);
            return null;
        }
    }

    @Override
    public List<PedidoDTO> fetchPedidos(String authToken) {
        String url = baseUrl + "/pedidos/?facilityCode=" + this.usCode;
        try {
            String decryptedJson = fetchAndDecrypt(url, authToken);
            return objectMapper.readValue(decryptedJson, objectMapper.getTypeFactory().constructCollectionType(List.class, PedidoDTO.class));
        } catch (Exception e) {
            log.error("Failed to fetch or decrypt Pedidos", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RespostaDTO> fetchRespostas(String authToken) {
        String url = baseUrl + "/respostas/?facilityCode=" + this.usCode;
        try {
            String decryptedJson = fetchAndDecrypt(url, authToken);
            return objectMapper.readValue(decryptedJson, objectMapper.getTypeFactory().constructCollectionType(List.class, RespostaDTO.class));
        } catch (Exception e) {
            log.error("Failed to fetch or decrypt Respostas", e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean markPedidosAsConsumed(List<String> pedidoUuids, String authToken) {
        String url = baseUrl + "/pedidos/mark-consumed";
        MarkConsumedPayload payload = new MarkConsumedPayload();
        payload.setPedidoUuids(pedidoUuids);
        try {
            return encryptAndPost(url, payload, authToken);
        } catch (Exception e) {
            log.error("Failed to mark Pedidos as consumed", e);
            return false;
        }
    }

    @Override
    public boolean markRespostasAsConsumed(List<String> respostaUuids, String authToken) {
        String url = baseUrl + "/respostas/mark-consumed";
        MarkConsumedPayload payload = new MarkConsumedPayload();
        payload.setRespostaUuids(respostaUuids);
        try {
            return encryptAndPost(url, payload, authToken);
        } catch (Exception e) {
            log.error("Failed to mark Respostas as consumed", e);
            return false;
        }
    }

    // --- PRIVATE HELPER METHODS ---

    private String fetchAndDecrypt(String url, String authToken) throws Exception {
        log.info("Fetching encrypted data from: " + url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + authToken);

            HttpResponse response = httpClient.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new RuntimeException("Failed to fetch data. Status: " + statusCode);
            }

            String jsonResponse = EntityUtils.toString(response.getEntity());
            EncryptedRequestDTO encryptedDTO = objectMapper.readValue(jsonResponse, EncryptedRequestDTO.class);

            // 1. Verify Signature (CRITICAL STEP)
            boolean isValid = MiddlewareCryptoUtil.verifySignatureOverString(encryptedDTO.getData(), encryptedDTO.getSignature(), this.serverPublicKey);
            if (!isValid) {
                throw new SecurityException("Signature verification failed! The data may have been tampered with.");
            }
            log.info("Signature verified successfully.");

            // 2. Decrypt Data
            String decryptedJson = MiddlewareCryptoUtil.decryptCompact(encryptedDTO.getData(), this.ourPrivateKey);
            log.info("Data decrypted successfully.");

            return decryptedJson;
        }
    }

    private boolean encryptAndPost(String url, Object payload, String authToken) throws Exception {
        log.info("Encrypting and posting data to: " + url);

        // 1. Convert payload object to JSON string
        String clearJsonPayload = objectMapper.writeValueAsString(payload);

        // 2. Encrypt the JSON payload using the server's public key
        String encryptedDataB64 = MiddlewareCryptoUtil.encryptCompact(clearJsonPayload, this.serverPublicKey);

        // 3. Sign the encrypted Base64 string using our private key
        String signatureB64 = MiddlewareCryptoUtil.signBase64String(encryptedDataB64, this.ourPrivateKey);

        // 4. Build the final DTO
        EncryptedRequestDTO finalPayload = new EncryptedRequestDTO(encryptedDataB64, signatureB64);
        String finalJson = objectMapper.writeValueAsString(finalPayload);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Authorization", "Bearer " + authToken);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(finalJson, StandardCharsets.UTF_8));

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Successfully posted encrypted payload. Status: " + statusCode);
                return true;
            } else {
                log.error("Failed to post encrypted payload. Status: " + statusCode);
                log.error("Response: " + EntityUtils.toString(response.getEntity()));
                return false;
            }
        }
    }

    private String getGP(String property) {
        return Context.getAdministrationService().getGlobalProperty(property);
    }
}