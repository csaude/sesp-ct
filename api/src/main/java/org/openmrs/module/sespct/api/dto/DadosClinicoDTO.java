package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosClinicoDTO {
	
	public DadosClinicoDTO() {
	}
	
	private String nome;
	
	private String categoriaProfissional;
	
	private String telefone;
	
	private String email;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCategoriaProfissional() {
		return categoriaProfissional;
	}
	
	public void setCategoriaProfissional(String categoriaProfissional) {
		this.categoriaProfissional = categoriaProfissional;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
}
