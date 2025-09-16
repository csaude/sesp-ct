package org.openmrs.module.sespct.api.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoDTO{
	private Integer id;
	private MetadadosPedidoDTO metadadosPedidoDTO;
	private DadosUtenteDTO dadosUtente;
	private DadosClinicoDTO dadosClinico;
	private ReportarFalenciaDTO reportarFalencia;

	private LinhaSolicitadaDTO linhaSolicitada;

	private List<HistoriaTarvDTO> historiaTarv;

	private List<DadosLaboratorioCD4DTO> dadosLaboratorioCD4;

	private List<DadosLaboratorioCargaViralDTO> dadosLaboratorioCargaViral;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public MetadadosPedidoDTO getMetadadosPedidoDTO() {
		return metadadosPedidoDTO;
	}

	public void setMetadadosPedidoDTO(MetadadosPedidoDTO metadadosPedidoDTO) {
		this.metadadosPedidoDTO = metadadosPedidoDTO;
	}

	public DadosUtenteDTO getDadosUtente() {
		return dadosUtente;
	}
	
	public void setDadosUtente(DadosUtenteDTO dadosUtente) {
		this.dadosUtente = dadosUtente;
	}
	
	public DadosClinicoDTO getDadosClinico() {
		return dadosClinico;
	}
	
	public void setDadosClinico(DadosClinicoDTO dadosClinico) {
		this.dadosClinico = dadosClinico;
	}
	
	public ReportarFalenciaDTO getReportarFalencia() {
		return reportarFalencia;
	}
	
	public void setReportarFalencia(ReportarFalenciaDTO reportarFalencia) {
		this.reportarFalencia = reportarFalencia;
	}
	
	public LinhaSolicitadaDTO getLinhaSolicitada() {
		return linhaSolicitada;
	}
	
	public void setLinhaSolicitada(LinhaSolicitadaDTO linhaSolicitada) {
		this.linhaSolicitada = linhaSolicitada;
	}
	
	public List<HistoriaTarvDTO> getHistoriaTarv() {
		return historiaTarv;
	}
	
	public void setHistoriaTarv(List<HistoriaTarvDTO> historiaTarv) {
		this.historiaTarv = historiaTarv;
	}
	
	public List<DadosLaboratorioCD4DTO> getDadosLaboratorioCD4() {
		return dadosLaboratorioCD4;
	}
	
	public void setDadosLaboratorioCD4(List<DadosLaboratorioCD4DTO> dadosLaboratorioCD4) {
		this.dadosLaboratorioCD4 = dadosLaboratorioCD4;
	}
	public List<DadosLaboratorioCargaViralDTO> getDadosLaboratorioCargaViral() {
		return dadosLaboratorioCargaViral;
	}
	
	public void setDadosLaboratorioCargaViral(List<DadosLaboratorioCargaViralDTO> dadosLaboratorioCargaViral) {
		this.dadosLaboratorioCargaViral = dadosLaboratorioCargaViral;
	}
	
}
