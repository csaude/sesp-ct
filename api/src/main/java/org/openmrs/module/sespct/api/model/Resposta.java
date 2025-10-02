package org.openmrs.module.sespct.api.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.*;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

@Entity
@Table(name = "sespct_resposta")
public class Resposta extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	public Resposta() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resposta_id")
	private Integer id;
	
	// Link to the original request remains the same
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;
	
	// --- Fields moved from nested objects to this main class ---
	
	@Column(name = "resposta_id_externo")
	private Integer respostaIdExterno;
	
	@Column(name = "processado_por", length = 255)
	private String processadoPor;
	
	@Column(name = "timestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime timestamp;
	
	@Column(name = "versao", length = 20)
	private String versao;
	
	@Column(name = "resposta", length = 255)
	private String resposta;
	
	@Column(name = "linha_terapeutica", length = 50)
	private String linhaTerapeutica;
	
	@Column(name = "esquema_aprovado", length = 100)
	private String esquemaAprovado;
	
	@Column(name = "data_resposta")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataResposta;
	
	@Column(name = "comentario", columnDefinition = "text")
	private String comentario;
	
	@Column(name = "autorizante", length = 255)
	private String autorizante;
	
	@Column(name = "email_autorizante", length = 255)
	private String emailAutorizante;
	
	@Column(name = "contacto_autorizante", length = 50)
	private String contactoAutorizante;
	
	@Column(name = "nivel_autorizacao", length = 50)
	private String nivelAutorizacao;
	
	@Column(name = "sincronizado")
	private Boolean sincronizado = false;
	
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
	
	public Integer getRespostaIdExterno() {
		return respostaIdExterno;
	}
	
	public void setRespostaIdExterno(Integer respostaIdExterno) {
		this.respostaIdExterno = respostaIdExterno;
	}
	
	public String getProcessadoPor() {
		return processadoPor;
	}
	
	public void setProcessadoPor(String processadoPor) {
		this.processadoPor = processadoPor;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getResposta() {
		return resposta;
	}
	
	public void setResposta(String resposta) {
		this.resposta = resposta;
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
	
	public LocalDateTime getDataResposta() {
		return dataResposta;
	}
	
	public void setDataResposta(LocalDateTime dataResposta) {
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
	
	public String getEmailAutorizante() {
		return emailAutorizante;
	}
	
	public void setEmailAutorizante(String emailAutorizante) {
		this.emailAutorizante = emailAutorizante;
	}
	
	public String getContactoAutorizante() {
		return contactoAutorizante;
	}
	
	public void setContactoAutorizante(String contactoAutorizante) {
		this.contactoAutorizante = contactoAutorizante;
	}
	
	public String getNivelAutorizacao() {
		return nivelAutorizacao;
	}
	
	public void setNivelAutorizacao(String nivelAutorizacao) {
		this.nivelAutorizacao = nivelAutorizacao;
	}
	
	public Boolean getSincronizado() {
		return sincronizado;
	}
	
	public void setSincronizado(Boolean sincronizado) {
		this.sincronizado = sincronizado;
	}
	
	@Transient
	public String getFormattedDataResposta() {
		if (dataResposta == null) {
			return "";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return dataResposta.format(formatter);
	}
	
	@Transient
	public String getFormattedTimestamp() {
		if (timestamp == null) {
			return "";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return timestamp.format(formatter);
	}
}
