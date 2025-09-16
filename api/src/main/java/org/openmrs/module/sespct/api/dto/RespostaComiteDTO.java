package org.openmrs.module.sespct.api.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaComiteDTO {
	private String resposta_texto;

	private String linhaTerapeutica;

	private String esquemaAprovado;

	private String dataResposta;

	private String comentario;

	private String autorizante;

	private String email;

	private String contacto;

	private String nivelAutorizacao;

	private String dataAprovacao;

	public String getRespostaTexto() {
		return resposta_texto;
	}
	
	public void setRespostaTexto(String resposta_texto) {
		this.resposta_texto = resposta_texto;
	}
	
	public String getLinhaTerapeutica() {
		return linhaTerapeutica;
	}
	
	public void setLinhaTerapeutica(String linhaTerapeutica) {
		this.linhaTerapeutica = linhaTerapeutica;
	}
	
	public String getEsquemaAprovado() {
		return esquemaAprovado;
	}
	
	public void setEsquemaAprovado(String esquemaAprovado) {
		this.esquemaAprovado = esquemaAprovado;
	}
	
	public String getDataResposta() {
		return dataResposta;
	}
	
	public void setDataResposta(String dataResposta) {
		this.dataResposta = dataResposta;
	}
	
	public String getComentario() {
		return comentario;
	}
	
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	public String getAutorizante() {
		return autorizante;
	}
	
	public void setAutorizante(String autorizante) {
		this.autorizante = autorizante;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getContacto() {
		return contacto;
	}
	
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	
	public String getNivelAutorizacao() {
		return nivelAutorizacao;
	}
	
	public void setNivelAutorizacao(String nivelAutorizacao) {
		this.nivelAutorizacao = nivelAutorizacao;
	}
	
	public String getDataAprovacao() {
		return dataAprovacao;
	}
	
	public void setDataAprovacao(String dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}
}
