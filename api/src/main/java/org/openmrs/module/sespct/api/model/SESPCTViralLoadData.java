package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity representing viral load laboratory data
 */
@Entity
@Table(name = "sespct_viral_load_data")
public class SESPCTViralLoadData extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "viral_load_data_id")
	private Integer viralLoadDataId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private SESPCTRequest sespRequest;
	
	@Column(name = "data_exame")
	@Temporal(TemporalType.DATE)
	private Date dataExame;
	
	@Column(name = "carga_viral")
	private Integer cargaViral;
	
	// Default constructor
	public SESPCTViralLoadData() {
	}
	
	// Constructor with parameters
	public SESPCTViralLoadData(SESPCTRequest sespRequest, Date dataExame, Integer cargaViral) {
		this.sespRequest = sespRequest;
		this.dataExame = dataExame;
		this.cargaViral = cargaViral;
	}
	
	// Getters and Setters
	@Override
	public Integer getId() {
		return viralLoadDataId;
	}
	
	@Override
	public void setId(Integer id) {
		this.viralLoadDataId = id;
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
	
	public Integer getCargaViral() {
		return cargaViral;
	}
	
	public void setCargaViral(Integer cargaViral) {
		this.cargaViral = cargaViral;
	}
}
