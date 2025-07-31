package org.openmrs.module.sespct.api.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sespct_metadata")
public class SESPCTMetadata {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metadata_id")
	private Long metadataId;
	
	@Column(name = "pedido_id")
	private String pedidoId;
	
	@Column(name = "data_submissao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSubmissao;
	
	@Column(name = "versao")
	private String versao;
	
	@Column(name = "origem")
	private String origem;
	
	@Column(name = "tipo_formulario")
	private String tipoFormulario;
	
	@Column(name = "solicitado_por")
	private String solicitadoPor;
	
	@Column(name = "estado")
	private String estado;
	
	// Getters and Setters
	public Long getMetadataId() {
		return metadataId;
	}
	
	public void setMetadataId(Long metadataId) {
		this.metadataId = metadataId;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public Date getDataSubmissao() {
		return dataSubmissao;
	}
	
	public void setDataSubmissao(Date dataSubmissao) {
		this.dataSubmissao = dataSubmissao;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String getTipoFormulario() {
		return tipoFormulario;
	}
	
	public void setTipoFormulario(String tipoFormulario) {
		this.tipoFormulario = tipoFormulario;
	}
	
	public String getSolicitadoPor() {
		return solicitadoPor;
	}
	
	public void setSolicitadoPor(String solicitadoPor) {
		this.solicitadoPor = solicitadoPor;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
}
