package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_dados_utente")
public class DadosUtente extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dados_utente_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private Pedido pedido;
	
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
	
	// --- Getters and Setters ---
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Pedido getPedido() {
		return pedido;
	}
	
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
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
}
