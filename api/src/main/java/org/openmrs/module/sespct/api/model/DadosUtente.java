package org.openmrs.module.sespct.api.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.Constants;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

@Entity
@Table(name = "sespct_dados_utente")
public class DadosUtente extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;

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
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataProvavelParto;
	
	@Column(name = "lactante", length = 10)
	private String lactante;
	
	@Column(name = "data_parto")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataParto;
	
	@Transient
	public boolean isGestante() {
		return gestante != null && gestante.equalsIgnoreCase(Constants.SIM);
	}
	
	@Transient
	public boolean isLactante() {
		return lactante != null && lactante.equalsIgnoreCase(Constants.SIM);
	}
	
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
	
	public LocalDateTime getDataProvavelParto() {
		return dataProvavelParto;
	}
	
	public void setDataProvavelParto(LocalDateTime dataProvavelParto) {
		this.dataProvavelParto = dataProvavelParto;
	}
	
	public String getLactante() {
		return lactante;
	}
	
	public void setLactante(String lactante) {
		this.lactante = lactante;
	}
	
	public LocalDateTime getDataParto() {
		return dataParto;
	}
	
	public void setDataParto(LocalDateTime dataParto) {
		this.dataParto = dataParto;
	}
}
