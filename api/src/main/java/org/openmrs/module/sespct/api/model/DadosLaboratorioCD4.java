package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "sespct_cd4_data")
public class DadosLaboratorioCD4 extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cd4_data_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
	@Column(name = "data_exame")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime data;
	
	@Column(name = "cd4")
	private Integer cd4;
	
	@Column(name = "cd4_percentagem")
	private Double cd4Percentagem;
	
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
	
	public LocalDateTime getData() {
		return data;
	}
	
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	
	public Integer getCd4() {
		return cd4;
	}
	
	public void setCd4(Integer cd4) {
		this.cd4 = cd4;
	}
	
	public Double getCd4Percentagem() {
		return cd4Percentagem;
	}
	
	public void setCd4Percentagem(Double cd4Percentagem) {
		this.cd4Percentagem = cd4Percentagem;
	}
}
