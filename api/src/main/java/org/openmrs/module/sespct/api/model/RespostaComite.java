package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Table(name = "sespct_resposta_comite")
public class RespostaComite extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resposta_comite_id")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "resposta_id", referencedColumnName = "resposta_id")
	private Resposta resposta;
	
	@Column(name = "resposta", length = 200)
	private String resposta_texto;
	
	@Column(name = "linha_terapeutica", length = 50)
	private String linhaTerapeutica;
	
	@Column(name = "esquema_aprovado", length = 100)
	private String esquemaAprovado;
	
	@Column(name = "data_resposta")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataResposta;
	
	@Column(name = "comentario", columnDefinition = "TEXT")
	private String comentario;
	
	@Column(name = "autorizante", length = 200)
	private String autorizante;
	
	@Column(name = "email", length = 100)
	private String email;
	
	@Column(name = "contacto", length = 20)
	private String contacto;
	
	@Column(name = "nivel_autorizacao", length = 50)
	private String nivelAutorizacao;
	
	@Column(name = "data_aprovacao")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataAprovacao;
	
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
	
	public LocalDateTime getDataAprovacao() {
		return dataAprovacao;
	}
	
	public void setDataAprovacao(LocalDateTime dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}
	
	@Transient
	public String getFormattedDataResposta() {
		if (dataResposta == null) {
			return "-"; // Return a dash if there is no response date
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return dataResposta.format(formatter);
	}
}
