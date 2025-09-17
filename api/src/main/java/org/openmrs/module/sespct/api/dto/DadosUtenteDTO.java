package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosUtenteDTO {
	
	public DadosUtenteDTO() {
	}
	
	private String nomeCompleto;
	
	private String iniciais;
	
	private String nid;
	
	private Double idade;
	
	private String estadioOms;
	
	private String estadioOmsMotivo;
	
	private String provincia;
	
	private String distrito;
	
	private String unidadeSanitaria;
	
	private String codigoUnidadeSanitaria;
	
	private Double peso;
	
	private String sexo;
	
	private String gestante;
	
	private String dataProvavelParto;
	
	private String lactante;
	
	private String dataParto;
	
	// --- Getters and Setters ---
	
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
	
	public String getDataProvavelParto() {
		return dataProvavelParto;
	}
	
	public void setDataProvavelParto(String dataProvavelParto) {
		this.dataProvavelParto = dataProvavelParto;
	}
	
	public String getLactante() {
		return lactante;
	}
	
	public void setLactante(String lactante) {
		this.lactante = lactante;
	}
	
	public String getDataParto() {
		return dataParto;
	}
	
	public void setDataParto(String dataParto) {
		this.dataParto = dataParto;
	}
}
