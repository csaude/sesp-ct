package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosLaboratorioCD4DTO{
	private String data;
	private Integer cd4;
	private Double cd4Percentagem;
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public Integer getCd4() {
		return cd4;
	}
	
	public void setCd4(Integer cd4) {
		this.cd4 = cd4;
	}
	
	public Double getCd4Percentagem() {
		return cd4Percentagem;
	}
	
	public void setCd4Percentagem(Double cd4Percentagem) {
		this.cd4Percentagem = cd4Percentagem;
	}
}
