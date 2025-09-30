package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaDTO {
	
	public RespostaDTO() {
	}
	
	@JsonIgnore
	private String uuid;
	
	@JsonProperty("pedido_id")
	private Integer pedidoId;
	
	@JsonProperty("autorizante")
	private String autorizante;
	
	@JsonProperty("comentario")
	private String comentario;
	
	@JsonProperty("contacto_autorizante")
	private String contactoAutorizante;
	
	@JsonProperty("data_resposta")
	private String dataResposta;
	
	@JsonProperty("email_autorizante")
	private String emailAutorizante;
	
	@JsonProperty("esquema_aprovado")
	private String esquemaAprovado;
	
	@JsonProperty("linha_terapeutica")
	private String linhaTerapeutica;
	
	@JsonProperty("nivel_autorizacao")
	private String nivelAutorizacao;
	
	@JsonProperty("processado_por")
	private String processadoPor;
	
	@JsonProperty("resposta")
	private String resposta;
	
	@JsonProperty("resposta_id")
	private Integer respostaId;
	
	@JsonProperty("timestamp")
	private String timestamp;
	
	@JsonProperty("versao")
	private String versao;
	
	// --- Getters and Setters ---
	
	public Integer getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public String getAutorizante() {
		return autorizante;
	}
	
	public void setAutorizante(String autorizante) {
		this.autorizante = autorizante;
	}
	
	public String getComentario() {
		return comentario;
	}
	
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	public String getContactoAutorizante() {
		return contactoAutorizante;
	}
	
	public void setContactoAutorizante(String contactoAutorizante) {
		this.contactoAutorizante = contactoAutorizante;
	}
	
	public String getDataResposta() {
		return dataResposta;
	}
	
	public void setDataResposta(String dataResposta) {
		this.dataResposta = dataResposta;
	}
	
	public String getEmailAutorizante() {
		return emailAutorizante;
	}
	
	public void setEmailAutorizante(String emailAutorizante) {
		this.emailAutorizante = emailAutorizante;
	}
	
	public String getEsquemaAprovado() {
		return esquemaAprovado;
	}
	
	public void setEsquemaAprovado(String esquemaAprovado) {
		this.esquemaAprovado = esquemaAprovado;
	}
	
	public String getLinhaTerapeutica() {
		return linhaTerapeutica;
	}
	
	public void setLinhaTerapeutica(String linhaTerapeutica) {
		this.linhaTerapeutica = linhaTerapeutica;
	}
	
	public String getNivelAutorizacao() {
		return nivelAutorizacao;
	}
	
	public void setNivelAutorizacao(String nivelAutorizacao) {
		this.nivelAutorizacao = nivelAutorizacao;
	}
	
	public String getProcessadoPor() {
		return processadoPor;
	}
	
	public void setProcessadoPor(String processadoPor) {
		this.processadoPor = processadoPor;
	}
	
	public String getResposta() {
		return resposta;
	}
	
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
	
	public Integer getRespostaId() {
		return respostaId;
	}
	
	public void setRespostaId(Integer respostaId) {
		this.respostaId = respostaId;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
