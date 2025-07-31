package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity representing CD4 laboratory data
 */
@Entity
@Table(name = "sespct_cd4_data")
public class SESPCTCd4Data extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cd4_data_id")
	private Integer cd4DataId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private SESPCTRequest sespRequest;
	
	@Column(name = "data_exame")
	@Temporal(TemporalType.DATE)
	private Date dataExame;
	
	@Column(name = "cd4_value")
	private Integer cd4;
	
	@Column(name = "cd4_percentagem")
	private Double cd4Percentagem;
	
	// Default constructor
	public SESPCTCd4Data() {
	}
	
	// Constructor with parameters
	public SESPCTCd4Data(SESPCTRequest sespRequest, Date dataExame, Integer cd4, Double cd4Percentagem) {
		this.sespRequest = sespRequest;
		this.dataExame = dataExame;
		this.cd4 = cd4;
		this.cd4Percentagem = cd4Percentagem;
	}
	
	// Getters and Setters
	@Override
	public Integer getId() {
		return cd4DataId;
	}
	
	@Override
	public void setId(Integer id) {
		this.cd4DataId = id;
	}
	
	public SESPCTRequest getSespRequest() {
		return sespRequest;
	}
	
	public void setSespRequest(SESPCTRequest sespRequest) {
		this.sespRequest = sespRequest;
	}
	
	public Date getDataExame() {
		return dataExame;
	}
	
	public void setDataExame(Date dataExame) {
		this.dataExame = dataExame;
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
