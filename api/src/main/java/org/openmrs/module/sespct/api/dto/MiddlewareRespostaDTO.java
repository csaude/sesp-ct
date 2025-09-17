package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// This class is used only to capture the outer "payload" string during the first stage of parsing.
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiddlewareRespostaDTO {
	
	private String payload;
	
	public MiddlewareRespostaDTO() {
	}
	
	public String getPayload() {
		return payload;
	}
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
}
