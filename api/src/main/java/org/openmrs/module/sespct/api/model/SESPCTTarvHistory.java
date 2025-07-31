package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity representing TARV history records
 */
@Entity
@Table(name = "sespct_tarv_history")
public class SESPCTTarvHistory extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tarv_history_id")
	private Integer tarvHistoryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private SESPCTRequest sespRequest;
	
	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;
	
	@Column(name = "data_termino")
	@Temporal(TemporalType.DATE)
	private Date dataTermino;
	
	@Column(name = "esquema_tarv", length = 100)
	private String esquemaTarv;
	
	// @OneToMany(mappedBy = "sespRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	// private List<SESPCTTarvHistory> historiaTarv;
	
	// Default constructor
	public SESPCTTarvHistory() {
	}
	
	// Constructor with parameters
	public SESPCTTarvHistory(SESPCTRequest sespRequest, Date dataInicio, Date dataTermino, String esquemaTarv) {
		this.sespRequest = sespRequest;
		this.dataInicio = dataInicio;
		this.dataTermino = dataTermino;
		this.esquemaTarv = esquemaTarv;
	}
	
	// Getters and Setters
	@Override
	public Integer getId() {
		return tarvHistoryId;
	}
	
	@Override
	public void setId(Integer id) {
		this.tarvHistoryId = id;
	}
	
	public SESPCTRequest getSespRequest() {
		return sespRequest;
	}
	
	public void setSespRequest(SESPCTRequest sespRequest) {
		this.sespRequest = sespRequest;
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
