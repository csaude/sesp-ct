package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosLaboratorioCargaViralDTO{

	private String data;
	private Long cargaViral;
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public Long getCargaViral() {
		return cargaViral;
	}
	
	public void setCargaViral(Long cargaViral) {
		this.cargaViral = cargaViral;
	}
}
