package org.openmrs.module.sespct.api.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.sespct.api.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "sespct_metadados_resposta")
public class MetadadosResposta extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "metadados_resposta_id")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "resposta_id", referencedColumnName = "resposta_id")
	private Resposta resposta;
	
	@Column(name = "resposta_id_external", nullable = false, length = 50)
	private String respostaId;
	
	@Column(name = "pedido_id_reference", nullable = false, length = 50)
	private String pedidoId;
	
	@Column(name = "versao", length = 10)
	private String versao;
	
	@Column(name = "timestamp")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime timestamp;
	
	@Column(name = "processado_por", length = 100)
	private String processadoPor;
	
	@Column(name = "ultima_sincronizacao")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime ultimaSincronizacao;
	
	// Getters and Setters
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Resposta getResposta() {
		return resposta;
	}
	
	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}
	
	public String getRespostaId() {
		return respostaId;
	}
	
	public void setRespostaId(String respostaId) {
		this.respostaId = respostaId;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(String pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public String getVersao() {
		return versao;
	}
	
	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getProcessadoPor() {
		return processadoPor;
	}
	
	public void setProcessadoPor(String processadoPor) {
		this.processadoPor = processadoPor;
	}
	
	public LocalDateTime getUltimaSincronizacao() {
		return ultimaSincronizacao;
	}
	
	public void setUltimaSincronizacao(LocalDateTime ultimaSincronizacao) {
		this.ultimaSincronizacao = ultimaSincronizacao;
	}
}
