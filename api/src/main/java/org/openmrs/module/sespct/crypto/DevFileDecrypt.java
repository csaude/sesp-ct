package org.openmrs.module.sespct.crypto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;

public final class DevFileDecrypt {
	
	public static void main(String[] args) throws Exception {
		// args: [baseDir] [jsonFile] [ctPublicPem] [clientPrivatePkcs8Pem]
		String base = args.length > 0 ? args[0] : "/home/voloide/Downloads/keys";
		String json = args.length > 1 ? args[1] : "sample_encrypted_data.json";
		String ctPubPem = args.length > 2 ? args[2] : "server_public_key.pem";
		String clientPrivPem = args.length > 3 ? args[3] : "client_private_key_pkcs8.pem";
		
		File jsonFile = new File(base, json);
		File ctPubFile = new File(base, ctPubPem);
		File clientPrivFile = new File(base, clientPrivPem);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonFile);
		
		// Expect: { "data": "<base64>", "signature": "<base64>" }
		String dataB64 = root.path("data").asText();
		String sigB64 = root.path("signature").asText();
		
		PublicKey ctPublic = CtCompactCrypto.readPublicKeyPem(new String(Files.readAllBytes(ctPubFile.toPath()),
		        StandardCharsets.UTF_8));
		PrivateKey clientPrivate = CtCompactCrypto.readPrivateKeyPem(new String(Files.readAllBytes(clientPrivFile.toPath()),
		        StandardCharsets.UTF_8));
		
		// 1) verify (CT signs the BASE64 string)
		boolean ok = CtCompactCrypto.verifySignatureBase64(dataB64, sigB64, ctPublic);
		System.out.println("Signature valid (over base64 string): " + ok);
		if (!ok) {
			System.err.println("!! Signature failed. Check you used CT's public key.");
			System.exit(1);
		}
		
		// 2) decrypt (RSA-OAEP SHA-256, IV=12, AES-GCM)
		byte[] clear = CtCompactCrypto.decryptCompact(dataB64, clientPrivate);
		
		// 3) print plaintext (pretty if JSON)
		try {
			Object obj = mapper.readValue(clear, Object.class);
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		}
		catch (Exception notJson) {
			System.out.println(new String(clear, StandardCharsets.UTF_8));
		}
	}
}
