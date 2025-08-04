package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_tarv_history")
public class HistoriaTarv extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tarv_history_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;
	
	@Column(name = "data_termino")
	@Temporal(TemporalType.DATE)
	private Date dataTermino;
	
	@Column(name = "esquema_tarv", length = 100)
	private String esquemaTarv;
	
	// --- Overrides from BaseOpenmrsData ---
	
	@Override
	public Integer getId() {
		return this.id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	// --- Getters and Setters ---
	
	public Pedido getPedido() {
		return pedido;
	}
	
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public Date getDataTermino() {
		return dataTermino;
	}
	
	public void setDataTermino(Date dataTermino) {
		this.dataTermino = dataTermino;
	}
	
	public String getEsquemaTarv() {
		return esquemaTarv;
	}
	
	public void setEsquemaTarv(String esquemaTarv) {
		this.esquemaTarv = esquemaTarv;
	}
}
