package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoriaTarvDTO {
	
	public HistoriaTarvDTO() {
	}
	
	private String dataInicio;
	
	private String dataTermino;
	
	private String esquemaTarv;
	
	public String getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public String getDataTermino() {
		return dataTermino;
	}
	
	public void setDataTermino(String dataTermino) {
		this.dataTermino = dataTermino;
	}
	
	public String getEsquemaTarv() {
		return esquemaTarv;
	}
	
	public void setEsquemaTarv(String esquemaTarv) {
		this.esquemaTarv = esquemaTarv;
	}
}
