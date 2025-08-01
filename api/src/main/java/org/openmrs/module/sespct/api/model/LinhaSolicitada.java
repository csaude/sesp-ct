package org.openmrs.module.sespct.api.model;

import javax.persistence.*;

@Entity
@Table(name = "sespct_linha_solicitada")
public class LinhaSolicitada {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "linha_solicitada_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private Pedido pedido;
	
	@Column(name = "linha", length = 20)
	private String linha;
	
	@Column(name = "anexo", columnDefinition = "LONGTEXT")
	private String anexo;
	
	// --- Getters and Setters ---
	public Pedido getPedido() {
		return pedido;
	}
	
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	public String getLinha() {
		return linha;
	}
	
	public void setLinha(String linha) {
		this.linha = linha;
	}
	
	public String getAnexo() {
		return anexo;
	}
	
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	
	// --- Optional: toString() ---
	
	@Override
	public String toString() {
		return "LinhaSolicitada{" + "linha='" + linha + '\'' + ", anexo='[base64 content]'" + '}';
	}
}
