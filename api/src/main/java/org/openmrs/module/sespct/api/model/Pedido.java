package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "sespct_pedido")
public class Pedido extends BaseOpenmrsData {
	
	public static final String ESTADO_NAO_PROCESSADO = "Não Processado";
	
	public static final String ESTADO_SEM_RESPOSTA = "Sem resposta";
	
	public static final String ESTADO_APROVADO = "Aprovado";
	
	public static final String CAUSA_NID_NAO_ENCONTRADO = "NID não encontrado";
	
	public static final String CAUSA_NID_DUPLICADO = "NID duplicado";
	
	public static final String SEXO_MASCULINO = "masculino";
	
	public static final String SEXO_FEMININO = "feminino";
	
	public Pedido() {
		this.dadosUtente = new DadosUtente();
		this.reportarFalencia = new ReportarFalencia();
		this.dadosClinico = new DadosClinico();
		this.linhaSolicitada = new LinhaSolicitada();
		this.respostas = new java.util.ArrayList<>();
		this.historiaTarv = new java.util.ArrayList<>();
		this.dadosLaboratorioCD4 = new java.util.ArrayList<>();
		this.dadosLaboratorioCargaViral = new java.util.ArrayList<>();
		this.dadosUtente.setPedido(this);
		this.dadosClinico.setPedido(this);
		this.linhaSolicitada.setPedido(this);
		this.reportarFalencia.setPedido(this);

	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_id")
	private Integer id;
	
	// Core metadata fields
	@Column(name = "pedidoId", nullable = false, unique = true, length = 50)
	private String pedidoId;
	
	@Column(name = "data_submissao")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataSubmissao;
	
	@Column(name = "versao", length = 10)
	private String versao;
	
	@Column(name = "origem", length = 50)
	private String origem;
	
	@Column(name = "tipo_formulario", length = 20)
	private String tipoFormulario;
	
	@Column(name = "solicitado_por", length = 100)
	private String solicitadoPor;
	
	@Column(name = "estado", length = 50)
	private String estado;
	
	@Column(name = "causa", length = 50)
	private String causa;
	
	// One-to-One relationships (each in its own table)
	@OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DadosUtente dadosUtente;
	
	@OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DadosClinico dadosClinico;
	
	@OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private ReportarFalencia reportarFalencia;
	
	@OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private LinhaSolicitada linhaSolicitada;
	
	// One-to-Many relationships for arrays
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<HistoriaTarv> historiaTarv;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DadosLaboratorioCD4> dadosLaboratorioCD4;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DadosLaboratorioCargaViral> dadosLaboratorioCargaViral;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Resposta> respostas;
	
	// --- Required Overrides ---
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	// --- Getters and Setters ---
	public String getCausa() {
		return causa;
	}
	
	public void setCausa(String causa) {
		this.causa = causa;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public LocalDateTime getDataSubmissao() {
		return dataSubmissao;
	}
	
	public void setDataSubmissao(LocalDateTime dataSubmissao) {
		this.dataSubmissao = dataSubmissao;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String getTipoFormulario() {
		return tipoFormulario;
	}
	
	public void setTipoFormulario(String tipoFormulario) {
		this.tipoFormulario = tipoFormulario;
	}
	
	public String getSolicitadoPor() {
		return solicitadoPor;
	}
	
	public void setSolicitadoPor(String solicitadoPor) {
		this.solicitadoPor = solicitadoPor;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public DadosUtente getDadosUtente() {
		return dadosUtente;
	}
	
	public void setDadosUtente(DadosUtente dadosUtente) {
		this.dadosUtente = dadosUtente;
	}
	
	public DadosClinico getDadosClinico() {
		return dadosClinico;
	}
	
	public void setDadosClinico(DadosClinico dadosClinico) {
		this.dadosClinico = dadosClinico;
	}
	
	public ReportarFalencia getReportarFalencia() {
		return reportarFalencia;
	}
	
	public void setReportarFalencia(ReportarFalencia reportarFalencia) {
		this.reportarFalencia = reportarFalencia;
	}
	
	public LinhaSolicitada getLinhaSolicitada() {
		return linhaSolicitada;
	}
	
	public void setLinhaSolicitada(LinhaSolicitada linhaSolicitada) {
		this.linhaSolicitada = linhaSolicitada;
	}
	
	public List<HistoriaTarv> getHistoriaTarv() {
		return historiaTarv;
	}
	
	public void setHistoriaTarv(List<HistoriaTarv> historiaTarv) {
		this.historiaTarv = historiaTarv;
	}
	
	public List<DadosLaboratorioCD4> getDadosLaboratorioCD4() {
		return dadosLaboratorioCD4;
	}
	
	public void setDadosLaboratorioCD4(List<DadosLaboratorioCD4> dadosLaboratorioCD4) {
		this.dadosLaboratorioCD4 = dadosLaboratorioCD4;
	}
	
	public List<DadosLaboratorioCargaViral> getDadosLaboratorioCargaViral() {
		return dadosLaboratorioCargaViral;
	}
	
	public void setDadosLaboratorioCargaViral(List<DadosLaboratorioCargaViral> dadosLaboratorioCargaViral) {
		this.dadosLaboratorioCargaViral = dadosLaboratorioCargaViral;
	}
	
	public List<Resposta> getRespostas() {
		return respostas;
	}
	
	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}
	
	@Transient
	public String getFormattedDataSubmissao() {
		if (dataSubmissao == null) {
			return "";
		}
		// This formatter will produce "04/09/2025"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return dataSubmissao.format(formatter);
	}
}
