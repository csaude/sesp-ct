package org.openmrs.module.sespct.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.MiddlewareApiService;
import org.openmrs.module.sespct.api.dto.*;
import org.openmrs.module.sespct.api.util.MiddlewareCryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

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
	
	private volatile boolean initialized = false;
	
	/**
	 * This method is called by Spring after the bean is created. It loads all necessary
	 * configuration from Global Properties.
	 */
	// Remove @PostConstruct and make it lazy initialization
	private synchronized void ensureInitialized() {
		if (!initialized) {
			initialize();
		}
	}
	
	private void initialize() {
		log.info("Initializing MiddlewareApiService...");
		// Check if Context is ready
		if (!Context.isSessionOpen()) {
			log.warn("OpenMRS Context is not ready yet. Skipping initialization.");
			return;
		}
		
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
			
			String registrationStatus = getGP("sespct.api.clientRegistered");
			// Always attempt client registration first to ensure we have the latest server public key
			
			if (!"true".equals(registrationStatus)) {
				log.info("Client not yet registered. Attempting registration...");
				if (registerClient()) {
					log.info("Client registration completed successfully.");
					serverPublicPem = getGP("sespct.api.serverPublicKey");
				} else {
					log.warn("Client registration failed. Will try to use existing server public key if available.");
				}
			}
			
			if (serverPublicPem != null && !serverPublicPem.isEmpty()) {
				serverPublicKey = MiddlewareCryptoUtil.readPublicKeyPem(serverPublicPem);
				log.info("Server public key loaded successfully.");
			} else {
				log.info("No server public key available. Encrypted operations will fail.");
			}
			initialized = true;
			log.info("MiddlewareApiService initialized successfully.");
		}
		catch (Exception e) {
			log.error("Failed to initialize MiddlewareApiService from Global Properties", e);
		}
	}
	
	@Override
    public String login() {
		ensureInitialized();
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

			post.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            HttpResponse response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                String token = rootNode.path("access_token").asText();
				if (token != null && !token.isEmpty()) {
					log.info("Successfully logged in and received auth token.");
					return token;
				} else {
					log.error("Login response did not contain access_token");
					log.debug("Login response: " + jsonResponse);
					return null;
				}
            } else {
                log.error("Login failed. Status code: " + statusCode);
                return null;
            }
        } catch (Exception e) {
            log.error("Error during login to middleware API", e);
            return null;
        }
    }
	
	public boolean registerClient() {
		String url = baseUrl + "/clients";
		log.info("Registering client at: " + url);

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type", "application/json");

			String ourPublicKeyPem = getGP("sespct.api.publicKey");
			if (ourPublicKeyPem == null || ourPublicKeyPem.isEmpty()) {
				log.error("Public key not configured for client registration");
				return false;
			}

			Map<String, String> payload = new HashMap<>();
			payload.put("usCode", this.usCode);
			payload.put("clientId", this.clientId);
			payload.put("clientSecret", this.clientSecret);
			payload.put("publicKey", ourPublicKeyPem);
			payload.put("salt", getGP("sespct.api.salt"));

			String jsonPayload = objectMapper.writeValueAsString(payload);
			post.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

			HttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());

			log.info("Registration response - Status: " + statusCode);

			if (statusCode >= 200 && statusCode < 300) {
				JsonNode rootNode = objectMapper.readTree(responseBody);
				JsonNode dataNode = rootNode.path("data");
				String serverPublicKey = dataNode.path("publicKey").asText();
				try {
					Context.getAdministrationService().setGlobalProperty("sespct.api.serverPublicKey", serverPublicKey);
					Context.getAdministrationService().setGlobalProperty("sespct.api.clientRegistered", "true");
					Context.flushSession();
					log.info("Server public key and registration status saved successfully.");
					return true;
				} catch (Exception e) {
					log.error("Failed to save global properties", e);
					return false;
				}

			} else {
				log.error("Client registration failed. Status: " + statusCode + ", Response: " + responseBody);
				return false;
			}
		} catch (Exception e) {
			log.error("Error during client registration", e);
			return false;
		}
	}
	
	@Override
	public List<PedidoDTO> fetchPedidos(String authToken) {
		ensureInitialized();
		String url = baseUrl + "/pedidos/?facilityCode=" + this.usCode;
		try {
			String decryptedJson = fetchAndDecrypt(url, authToken);

			JsonNode rootNode = objectMapper.readTree(decryptedJson);
			JsonNode contentArrayNode = rootNode.path("content");

			List<MiddlewarePedidoDTO> middlewarePedidos = objectMapper.convertValue(
					contentArrayNode,
					objectMapper.getTypeFactory().constructCollectionType(List.class, MiddlewarePedidoDTO.class)
			);

			List<PedidoDTO> finalPedidos = new ArrayList<>();
			for (MiddlewarePedidoDTO middlewarePedido : middlewarePedidos) {
				String payloadJson = middlewarePedido.getPayload();
				JsonNode payloadRootNode = objectMapper.readTree(payloadJson);
				JsonNode dadosPedidoNode = payloadRootNode.path("dadosPedido");
				PedidoDTO pedido = objectMapper.treeToValue(dadosPedidoNode, PedidoDTO.class);

				if (payloadRootNode.has("id")) {
					pedido.setId(payloadRootNode.get("id").asText());
				}

				finalPedidos.add(pedido);
			}

			return finalPedidos;
		}
		catch (Exception e) {
			log.error("Failed to fetch or decrypt Pedidos", e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<RespostaDTO> fetchRespostas(String authToken) {
		ensureInitialized();
		String url = baseUrl + "/respostas/?facilityCode=" + this.usCode;
		try {
			String decryptedJson = fetchAndDecrypt(url, authToken);
			
			JsonNode rootNode = objectMapper.readTree(decryptedJson);
			JsonNode contentArrayNode = rootNode.path("content");

			// STAGE 1: Parse the outer structure to get the payload strings
			List<MiddlewareRespostaDTO> middlewareRespostas = objectMapper.convertValue(
					contentArrayNode,
					objectMapper.getTypeFactory().constructCollectionType(List.class, MiddlewareRespostaDTO.class)
			);

			// STAGE 2: Loop through, parse the inner payload, and build the final list
			List<RespostaDTO> finalRespostas = new ArrayList<>();
			for (MiddlewareRespostaDTO middlewareResposta : middlewareRespostas) {
				String payloadJson = middlewareResposta.getPayload();
				JsonNode payloadRootNode = objectMapper.readTree(payloadJson);

				// Navigate into the "dadosResposta" object
				JsonNode dadosRespostaNode = payloadRootNode.path("dadosResposta");

				// Convert the "dadosResposta" object into your final RespostaDTO
				RespostaDTO resposta = objectMapper.treeToValue(dadosRespostaNode, RespostaDTO.class);
				finalRespostas.add(resposta);
			}

			return finalRespostas;

		}
		catch (Exception e) {
			log.error("Failed to fetch or decrypt Respostas", e);
			return Collections.emptyList();
		}
	}
	
	@Override
	public boolean markPedidosAsConsumed(List<String> pedidoUuids, String authToken) {
		ensureInitialized();
		String url = baseUrl + "/pedidos/mark-consumed";
		MarkConsumedPayload payload = new MarkConsumedPayload();
		payload.setPedidoUuids(pedidoUuids);
		try {
			return encryptAndPost(url, payload, authToken);
		}
		catch (Exception e) {
			log.error("Failed to mark Pedidos as consumed", e);
			return false;
		}
	}
	
	@Override
	public boolean markRespostasAsConsumed(List<String> respostaUuids, String authToken) {
		ensureInitialized();
		String url = baseUrl + "/respostas/mark-consumed";
		MarkConsumedPayload payload = new MarkConsumedPayload();
		payload.setRespostaUuids(respostaUuids);
		try {
			return encryptAndPost(url, payload, authToken);
		}
		catch (Exception e) {
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
