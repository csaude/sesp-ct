package org.openmrs.module.sespct.api.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

@Entity
@Table(name = "sespct_tarv_history")
public class HistoriaTarv extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tarv_history_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
	@Column(name = "data_inicio")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataInicio;
	
	@Column(name = "data_termino")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime dataTermino;
	
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
	
	public LocalDateTime getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(LocalDateTime dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public LocalDateTime getDataTermino() {
		return dataTermino;
	}
	
	public void setDataTermino(LocalDateTime dataTermino) {
		this.dataTermino = dataTermino;
	}
	
	public String getEsquemaTarv() {
		return esquemaTarv;
	}
	
	public void setEsquemaTarv(String esquemaTarv) {
		this.esquemaTarv = esquemaTarv;
	}
}
