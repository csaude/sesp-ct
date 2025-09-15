package org.openmrs.module.sespct.api.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;

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
	
	// Link to the original request
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;
	
	// One-to-One relationships
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private MetadadosResposta metadados;
	
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private RespostaComite respostaComite;
	
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Notificacoes notificacoes;
	
	@Column(name = "sincronizado")
	private Boolean sincronizado = false;
	
	// Getters and Setters
	@Override
	public Integer getId() {
		return id;
	}
	
	public Boolean getSincronizado() {
		return sincronizado;
	}
	
	public void setSincronizado(Boolean sincronizado) {
		this.sincronizado = sincronizado;
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
	
	public MetadadosResposta getMetadados() {
		return metadados;
	}
	
	public void setMetadados(MetadadosResposta metadados) {
		this.metadados = metadados;
	}
	
	public RespostaComite getRespostaComite() {
		return respostaComite;
	}
	
	public void setRespostaComite(RespostaComite respostaComite) {
		this.respostaComite = respostaComite;
	}
	
	public Notificacoes getNotificacoes() {
		return notificacoes;
	}
	
	public void setNotificacoes(Notificacoes notificacoes) {
		this.notificacoes = notificacoes;
	}
	
}
