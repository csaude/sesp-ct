package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_resposta_comite")
public class RespostaComite extends BaseOpenmrsData {
	
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataResposta;
	
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAprovacao;
	
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
	
	public Date getDataResposta() {
		return dataResposta;
	}
	
	public void setDataResposta(Date dataResposta) {
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
	
	public Date getDataAprovacao() {
		return dataAprovacao;
	}
	
	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}
}
