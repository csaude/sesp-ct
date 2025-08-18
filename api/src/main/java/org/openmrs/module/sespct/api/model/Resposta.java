package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_resposta")
public class Resposta extends BaseOpenmrsData {
	
	public Resposta() {
		this.metadados = new MetadadosResposta();
		this.respostaComite = new RespostaComite();
		this.notificacoes = new Notificacoes();
		this.metadados.setResposta(this);
		this.respostaComite.setResposta(this);
		this.notificacoes.setResposta(this);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resposta_id")
	private Integer id;
	
	// Link to the original request
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id")
	private Pedido pedido;
	
	// One-to-One relationships
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private MetadadosResposta metadados;
	
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private RespostaComite respostaComite;
	
	@OneToOne(mappedBy = "resposta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Notificacoes notificacoes;
	
	// Getters and Setters
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
