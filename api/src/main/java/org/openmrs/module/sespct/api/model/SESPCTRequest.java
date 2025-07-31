package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sespct_request")
public class SESPCTRequest extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id")
	private Integer requestId;
	
	// Metadados fields
	@Column(name = "pedido_id", nullable = false, length = 50)
	private String pedidoId;
	
	@Column(name = "data_submissao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSubmissao;
	
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
	
	// Dados do Utente fields
	@Column(name = "nome_completo", length = 200)
	private String nomeCompleto;
	
	@Column(name = "iniciais", length = 10)
	private String iniciais;
	
	@Column(name = "nid", length = 50)
	private String nid;
	
	@Column(name = "idade")
	private Double idade;
	
	@Column(name = "estadio_oms", length = 50)
	private String estadioOms;
	
	@Column(name = "estadio_oms_motivo", columnDefinition = "TEXT")
	private String estadioOmsMotivo;
	
	@Column(name = "provincia", length = 100)
	private String provincia;
	
	@Column(name = "distrito", length = 100)
	private String distrito;
	
	@Column(name = "unidade_sanitaria", length = 200)
	private String unidadeSanitaria;
	
	@Column(name = "codigo_unidade_sanitaria", length = 50)
	private String codigoUnidadeSanitaria;
	
	@Column(name = "peso")
	private Double peso;
	
	@Column(name = "sexo", length = 20)
	private String sexo;
	
	@Column(name = "gestante", length = 10)
	private String gestante;
	
	@Column(name = "data_provavel_parto")
	@Temporal(TemporalType.DATE)
	private Date dataProvavelParto;
	
	@Column(name = "lactante", length = 10)
	private String lactante;
	
	@Column(name = "data_parto")
	@Temporal(TemporalType.DATE)
	private Date dataParto;
	
	// Reportar Falencia fields
	@Column(name = "historia_clinica", columnDefinition = "TEXT")
	private String historiaClinica;
	
	@Column(name = "historia_adesao", columnDefinition = "TEXT")
	private String historiaAdesao;
	
	@Column(name = "tratamento_tb_ativo", length = 10)
	private String tratamentoTbAtivo;
	
	// Dados Clinico fields
	@Column(name = "clinico_nome", length = 200)
	private String clinicoNome;
	
	@Column(name = "clinico_categoria", length = 100)
	private String clinicoCategoria;
	
	@Column(name = "clinico_telefone", length = 20)
	private String clinicoTelefone;
	
	@Column(name = "clinico_email", length = 100)
	private String clinicoEmail;
	
	// Linha Solicitada fields
	@Column(name = "linha_solicitada", length = 20)
	private String linhaSolicitada;
	
	@Column(name = "anexo", columnDefinition = "LONGTEXT")
	private String anexo;
	
	// One-to-many relationships for complex nested data
	@OneToMany(mappedBy = "sespRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<SESPCTTarvHistory> historiaTarv;
	
	@OneToMany(mappedBy = "sespRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<SESPCTCd4Data> dadosLaboratorioCD4;
	
	@OneToMany(mappedBy = "sespRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<SESPCTViralLoadData> dadosLaboratorioCargaViral;
	
	// Default constructor
	public SESPCTRequest() {
	}
	
	// Getters and Setters
	@Override
	public Integer getId() {
		return requestId;
	}
	
	@Override
	public void setId(Integer id) {
		this.requestId = id;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public Date getDataSubmissao() {
		return dataSubmissao;
	}
	
	public void setDataSubmissao(Date dataSubmissao) {
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
	
	public String getNomeCompleto() {
		return nomeCompleto;
	}
	
	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}
	
	public String getIniciais() {
		return iniciais;
	}
	
	public void setIniciais(String iniciais) {
		this.iniciais = iniciais;
	}
	
	public String getNid() {
		return nid;
	}
	
	public void setNid(String nid) {
		this.nid = nid;
	}
	
	public Double getIdade() {
		return idade;
	}
	
	public void setIdade(Double idade) {
		this.idade = idade;
	}
	
	public String getEstadioOms() {
		return estadioOms;
	}
	
	public void setEstadioOms(String estadioOms) {
		this.estadioOms = estadioOms;
	}
	
	public String getEstadioOmsMotivo() {
		return estadioOmsMotivo;
	}
	
	public void setEstadioOmsMotivo(String estadioOmsMotivo) {
		this.estadioOmsMotivo = estadioOmsMotivo;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public String getDistrito() {
		return distrito;
	}
	
	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}
	
	public String getUnidadeSanitaria() {
		return unidadeSanitaria;
	}
	
	public void setUnidadeSanitaria(String unidadeSanitaria) {
		this.unidadeSanitaria = unidadeSanitaria;
	}
	
	public String getCodigoUnidadeSanitaria() {
		return codigoUnidadeSanitaria;
	}
	
	public void setCodigoUnidadeSanitaria(String codigoUnidadeSanitaria) {
		this.codigoUnidadeSanitaria = codigoUnidadeSanitaria;
	}
	
	public Double getPeso() {
		return peso;
	}
	
	public void setPeso(Double peso) {
		this.peso = peso;
	}
	
	public String getSexo() {
		return sexo;
	}
	
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	public String getGestante() {
		return gestante;
	}
	
	public void setGestante(String gestante) {
		this.gestante = gestante;
	}
	
	public Date getDataProvavelParto() {
		return dataProvavelParto;
	}
	
	public void setDataProvavelParto(Date dataProvavelParto) {
		this.dataProvavelParto = dataProvavelParto;
	}
	
	public String getLactante() {
		return lactante;
	}
	
	public void setLactante(String lactante) {
		this.lactante = lactante;
	}
	
	public Date getDataParto() {
		return dataParto;
	}
	
	public void setDataParto(Date dataParto) {
		this.dataParto = dataParto;
	}
	
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
	
	public String getClinicoNome() {
		return clinicoNome;
	}
	
	public void setClinicoNome(String clinicoNome) {
		this.clinicoNome = clinicoNome;
	}
	
	public String getClinicoCategoria() {
		return clinicoCategoria;
	}
	
	public void setClinicoCategoria(String clinicoCategoria) {
		this.clinicoCategoria = clinicoCategoria;
	}
	
	public String getClinicoTelefone() {
		return clinicoTelefone;
	}
	
	public void setClinicoTelefone(String clinicoTelefone) {
		this.clinicoTelefone = clinicoTelefone;
	}
	
	public String getClinicoEmail() {
		return clinicoEmail;
	}
	
	public void setClinicoEmail(String clinicoEmail) {
		this.clinicoEmail = clinicoEmail;
	}
	
	public String getLinhaSolicitada() {
		return linhaSolicitada;
	}
	
	public void setLinhaSolicitada(String linhaSolicitada) {
		this.linhaSolicitada = linhaSolicitada;
	}
	
	public String getAnexo() {
		return anexo;
	}
	
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	
	public List<SESPCTTarvHistory> getHistoriaTarv() {
		return historiaTarv;
	}
	
	public void setHistoriaTarv(List<SESPCTTarvHistory> historiaTarv) {
		this.historiaTarv = historiaTarv;
	}
	
	public List<SESPCTCd4Data> getDadosLaboratorioCD4() {
		return dadosLaboratorioCD4;
	}
	
	public void setDadosLaboratorioCD4(List<SESPCTCd4Data> dadosLaboratorioCD4) {
		this.dadosLaboratorioCD4 = dadosLaboratorioCD4;
	}
	
	public List<SESPCTViralLoadData> getDadosLaboratorioCargaViral() {
		return dadosLaboratorioCargaViral;
	}
	
	public void setDadosLaboratorioCargaViral(List<SESPCTViralLoadData> dadosLaboratorioCargaViral) {
		this.dadosLaboratorioCargaViral = dadosLaboratorioCargaViral;
	}
}
