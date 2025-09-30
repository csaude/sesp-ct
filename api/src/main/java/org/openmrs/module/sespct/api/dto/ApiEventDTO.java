package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// This new DTO maps the entire webhook event payload
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiEventDTO {
	
	@JsonProperty("request_id")
	private String requestId;
	
	@JsonProperty("response")
	private RespostaDTO response;
	
	// Getters and Setters
	public String getRequestId() {
		return requestId;
	}
	
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public RespostaDTO getResponse() {
		return response;
	}
	
	public void setResponse(RespostaDTO response) {
		this.response = response;
	}
}
