package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_viral_load_data")
public class DadosLaboratorioCargaViral extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "viral_load_data_id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
	@Column(name = "data_exame")
	@Temporal(TemporalType.DATE)
	private Date data;
	
	@Column(name = "carga_viral")
	private Long cargaViral;
	
	@Override
	public Integer getId() {
		return this.id;
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
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public Long getCargaViral() {
		return cargaViral;
	}
	
	public void setCargaViral(Long cargaViral) {
		this.cargaViral = cargaViral;
	}
}
