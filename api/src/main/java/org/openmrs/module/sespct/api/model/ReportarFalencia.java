package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;

@Entity
@Table(name = "sespct_reportar_falencia")
public class ReportarFalencia extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reportar_falencia_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	private Pedido pedido;
	
	@Column(name = "historia_clinica", columnDefinition = "TEXT")
	private String historiaClinica;
	
	@Column(name = "historia_adesao", columnDefinition = "TEXT")
	private String historiaAdesao;
	
	@Column(name = "tratamento_tb_ativo", length = 10)
	private String tratamentoTbAtivo;
	
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
	
	public String getHistoriaClinica() {
		return historiaClinica;
	}
	
	public void setHistoriaClinica(String historiaClinica) {
		this.historiaClinica = historiaClinica;
	}
	
	public String getHistoriaAdesao() {
		return historiaAdesao;
	}
	
	public void setHistoriaAdesao(String historiaAdesao) {
		this.historiaAdesao = historiaAdesao;
	}
	
	public String getTratamentoTbAtivo() {
		return tratamentoTbAtivo;
	}
	
	public void setTratamentoTbAtivo(String tratamentoTbAtivo) {
		this.tratamentoTbAtivo = tratamentoTbAtivo;
	}
	
	// --- Optional: toString() ---
	
	@Override
	public String toString() {
		return "ReportarFalencia{" + "historiaClinica='" + historiaClinica + '\'' + ", historiaAdesao='" + historiaAdesao
		        + '\'' + ", tratamentoTbAtivo='" + tratamentoTbAtivo + '\'' + '}';
	}
}
