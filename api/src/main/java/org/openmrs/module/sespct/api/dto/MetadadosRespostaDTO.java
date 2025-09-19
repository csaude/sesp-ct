package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadadosRespostaDTO {
	
	public MetadadosRespostaDTO() {
	}
	
	private String respostaId;
	
	private String pedidoId;
	
	private String versao;
	
	private String timestamp;
	
	private String processadoPor;
	
	private String ultimaSincronizacao;
	
	public String getRespostaId() {
		return respostaId;
	}
	
	public void setRespostaId(String respostaId) {
		this.respostaId = respostaId;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getProcessadoPor() {
		return processadoPor;
	}
	
	public void setProcessadoPor(String processadoPor) {
		this.processadoPor = processadoPor;
	}
	
	public String getUltimaSincronizacao() {
		return ultimaSincronizacao;
	}
	
	public void setUltimaSincronizacao(String ultimaSincronizacao) {
		this.ultimaSincronizacao = ultimaSincronizacao;
	}
}
