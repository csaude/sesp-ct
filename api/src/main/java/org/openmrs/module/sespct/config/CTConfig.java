package org.openmrs.module.sespct.config;

import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class CTConfig {
	
	private String gp(String key, String def) {
		try {
			String v = Context.getAdministrationService().getGlobalProperty(key);
			return (v == null || v.trim().isEmpty()) ? def : v.trim();
		}
		catch (Exception e) {
			return def;
		}
	}
	
	private String gpOrFile(String key, String def) {
		String v = gp(key, null);
		if (v == null || v.length() == 0)
			return def;
		if (v.startsWith("file:")) {
			try {
				String path = v.substring(5);
				byte[] bytes = Files.readAllBytes(Paths.get(path));
				return new String(bytes, StandardCharsets.UTF_8);
			}
			catch (Exception e) {
				return def; // fallback se não conseguir ler o ficheiro
			}
		}
		return v;
	}
	
	// URLs & OAuth
	public String getCtBaseUrl() {
		return gp("sesp.ct.baseUrl", "https://comitetarvmisau.co.mz");
	}
	
	public String getTokenUrl() {
		return gp("sesp.ct.oauth.tokenUrl", getCtBaseUrl() + "/oauth/token");
	}
	
	public String getOauthClientId() {
		return gp("sesp.ct.oauth.clientId", "");
	}
	
	public String getOauthClientSecret() {
		return gp("sesp.ct.oauth.clientSecret", "");
	}
	
	// Keys (carregadas do GP ou de ficheiro:)
	public String getCtPublicPem() {
		return gpOrFile("sesp.ct.keys.ctPublicPem", "");
	}
	
	public String getOmrsPublicPem() {
		return gpOrFile("sesp.ct.keys.omrsPublicPem", "");
	}
	
	public String getOmrsPrivatePem() {
		return gpOrFile("sesp.ct.keys.omrsPrivatePem", "");
	}
	
	public String getClientKeyId() {
		return gp("sesp.ct.keys.clientKeyId", "openmrs-dev-key-1");
	}
	
	// Webhook & defaults
	public String getWebhookUrl() {
		return gp("sesp.ct.webhook.url", "http://localhost:8080/openmrs/public/webhook/e-ft");
	}
	
	public String getDefaultFacility() {
		return gp("sesp.ct.facilityCode", "HCM001");
	}
	
	public String getSinceIso() {
		return gp("sesp.ct.since", "2024-01-01T00:00:00Z");
	}
}
