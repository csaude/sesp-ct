package org.openmrs.module.sespct.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.SespctApiService;
import org.openmrs.module.sespct.api.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Transactional
public class SespctApiServiceImpl extends BaseOpenmrsService implements SespctApiService {
	
	private static final Logger log = LoggerFactory.getLogger(SespctApiServiceImpl.class);
	
	private String accessToken;
	
	private long tokenExpiryTime;
	
	private String getGlobalProperty(String key) {
		return Context.getAdministrationService().getGlobalProperty(key);
	}
	
	private void ensureValidToken() throws Exception {
        if (accessToken == null || System.currentTimeMillis() >= (tokenExpiryTime - 60000)) {
            log.info("Access token is missing or expired. Fetching a new one.");

            String clientId = getGlobalProperty("sespct.api.clientId");
            String clientSecret = getGlobalProperty("sespct.api.clientSecret");
            String scopes = getGlobalProperty("sespct.api.scopes");
            String baseUrl = getGlobalProperty("sespct.api.baseUrl");

            // ... (rest of the method is the same) ...
            URL url = new URL(baseUrl + "/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String data = "grant_type=client_credentials&scope=" + URLEncoder.encode(scopes, "UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder content = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
            }
            conn.disconnect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content.toString());
            this.accessToken = root.path("access_token").asText();
            long expiresIn = root.path("expires_in").asLong();
            this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000);

            log.info("Successfully obtained new access token. Token starts with: '{}...'",
                    this.accessToken.substring(0, Math.min(this.accessToken.length(), 8)));
        }
    }
	
	@Override
    public String fetchPedidoById(String externalId) {
        try {
            ensureValidToken();

            String baseUrl = getGlobalProperty("sespct.api.baseUrl");
            URL url = new URL(baseUrl + "/api/v1/pedido-troca-linhas/" + externalId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            conn.setRequestProperty("scope", "read");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Failed to fetch pedido {}. Server responded with code: {}", externalId, conn.getResponseCode());
                return null;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
            }
            conn.disconnect();

            // --- NEW LOGGING ---
            log.info("Received raw API response: {}", content.toString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content.toString());
            String encryptedData = root.path("data").asText();
            String signature = root.path("signature").asText();

            log.info("Extracted encrypted data (first 20 chars): '{}...'", encryptedData.substring(0, 20));
            log.info("Extracted signature (first 20 chars): '{}...'", signature.substring(0, 20));

            String clientPrivateKeyPem = getGlobalProperty("sespct.api.clientPrivateKey");
            String serverPublicKeyPem = getGlobalProperty("sespct.api.serverPublicKey");

            // Using DEBUG level for keys so they don't clutter production logs
            log.debug("Attempting to load client private key: [{}]", clientPrivateKeyPem);
            log.debug("Attempting to load server public key: [{}]", serverPublicKeyPem);

            // Decrypt the response
            PrivateKey clientPrivateKey = CryptoUtil.loadPrivateKey(clientPrivateKeyPem);
            PublicKey serverPublicKey = CryptoUtil.loadPublicKey(serverPublicKeyPem);

            String decryptedJson = CryptoUtil.decryptAndVerify(encryptedData, signature, clientPrivateKey, serverPublicKey);
            log.info("Successfully fetched and decrypted Pedido {}", externalId);
            log.info("Decrypted content: {}", decryptedJson);

            return decryptedJson;

        } catch (Exception e) {
            log.error("Failed to fetch or decrypt Pedido with ID: " + externalId, e);
            return null;
        }
    }
	
	@Override
	public void syncPedidosFromApi() {
		log.info("Starting sync process from SESP-CT API...");
		String decryptedPedidoJson = fetchPedidoById("1001");
		
		if (decryptedPedidoJson != null) {
			log.info("Sync was successful. Data is ready to be parsed and saved.");
		} else {
			log.warn("Sync failed. No data was returned from the fetch process.");
		}
		log.info("Sync process finished.");
	}
}
