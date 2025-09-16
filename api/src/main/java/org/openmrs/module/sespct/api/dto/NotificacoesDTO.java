package org.openmrs.module.sespct.api.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificacoesDTO{
	private Boolean webhookEntregue;

	private Boolean emailEnviado;

	private Boolean smsEnviado;
	private String dataNotificacao;

	public Boolean getWebhookEntregue() {
		return webhookEntregue;
	}
	
	public void setWebhookEntregue(Boolean webhookEntregue) {
		this.webhookEntregue = webhookEntregue;
	}
	
	public Boolean getEmailEnviado() {
		return emailEnviado;
	}
	
	public void setEmailEnviado(Boolean emailEnviado) {
		this.emailEnviado = emailEnviado;
	}
	
	public Boolean getSmsEnviado() {
		return smsEnviado;
	}
	
	public void setSmsEnviado(Boolean smsEnviado) {
		this.smsEnviado = smsEnviado;
	}
	
	public String getDataNotificacao() {
		return dataNotificacao;
	}
	
	public void setDataNotificacao(String dataNotificacao) {
		this.dataNotificacao = dataNotificacao;
	}
}
