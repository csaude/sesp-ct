package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaDTO {
	
	public RespostaDTO() {
	}
	
	private String pedidoId;
	
	private MetadadosRespostaDTO metadados;
	
	private RespostaComiteDTO respostaComite;
	
	private NotificacoesDTO notificacoes;
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public MetadadosRespostaDTO getMetadados() {
		return metadados;
	}
	
	public void setMetadados(MetadadosRespostaDTO metadados) {
		this.metadados = metadados;
	}
	
	public RespostaComiteDTO getRespostaComite() {
		return respostaComite;
	}
	
	public void setRespostaComite(RespostaComiteDTO respostaComite) {
		this.respostaComite = respostaComite;
	}
	
	public NotificacoesDTO getNotificacoes() {
		return notificacoes;
	}
	
	public void setNotificacoes(NotificacoesDTO notificacoes) {
		this.notificacoes = notificacoes;
	}
	
}
