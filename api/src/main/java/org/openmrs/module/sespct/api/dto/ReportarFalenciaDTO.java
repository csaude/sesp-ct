package org.openmrs.module.sespct.api.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportarFalenciaDTO {
	private String historiaClinica;
	private String historiaAdesao;

	private String tratamentoTbAtivo;

	public String getHistoriaClinica() {
		return historiaClinica;
	}
	
	public void setHistoriaClinica(String historiaClinica) {
		this.historiaClinica = historiaClinica;
	}
	
	public String getHistoriaAdesao() {
		return historiaAdesao;
	}
	
	public void setHistoriaAdesao(String historiaAdesao) {
		this.historiaAdesao = historiaAdesao;
	}
	
	public String getTratamentoTbAtivo() {
		return tratamentoTbAtivo;
	}
	
	public void setTratamentoTbAtivo(String tratamentoTbAtivo) {
		this.tratamentoTbAtivo = tratamentoTbAtivo;
	}

}
