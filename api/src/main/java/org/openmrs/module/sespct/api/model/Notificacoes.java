package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "sespct_notificacoes")
public class Notificacoes extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notificacoes_id")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "resposta_id", referencedColumnName = "resposta_id")
	private Resposta resposta;
	
	@Column(name = "webhook_entregue")
	private Boolean webhookEntregue;
	
	@Column(name = "email_enviado")
	private Boolean emailEnviado;
	
	@Column(name = "sms_enviado")
	private Boolean smsEnviado;
	
	@Column(name = "data_notificacao")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataNotificacao;
	
	// Getters and Setters
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Resposta getResposta() {
		return resposta;
	}
	
	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}
	
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
	
	public LocalDateTime getDataNotificacao() {
		return dataNotificacao;
	}
	
	public void setDataNotificacao(LocalDateTime dataNotificacao) {
		this.dataNotificacao = dataNotificacao;
	}
}
