package org.openmrs.module.sespct.api.builder;

import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.util.Constants;
import org.openmrs.module.sespct.api.util.StringHelper;
import org.openmrs.module.sespct.api.util.TarvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Only build and configure observations (Obs)
 */
public class ObsBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ObsBuilder.class);
	
	private final Encounter encounter;
	
	private final Patient patient;
	
	public ObsBuilder(Encounter encounter, Patient patient) {
		this.encounter = encounter;
		this.patient = patient;
	}
	
	// Apenas criar observação de texto
	public ObsBuilder addTextObs(String conceptUuid, String value) {
		if (value == null || value.trim().isEmpty()) {
			return this;
		}
		
		Obs obs = createBaseObs(conceptUuid);
		obs.setValueText(value.trim());
		encounter.addObs(obs);
		
		return this;
	}
	
	// Apenas criar observação numérica
	public ObsBuilder addNumericObs(String conceptUuid, Double value) {
		if (value == null || value <= 0) {
			return this;
		}
		
		Obs obs = createBaseObs(conceptUuid);
		obs.setValueNumeric(value);
		encounter.addObs(obs);
		
		return this;
	}
	
	// Apenas criar observação booleana (Sim/Não)
	public ObsBuilder addBooleanObs(String conceptUuid, Boolean value) {
		if (value == null) {
			return this;
		}
		
		Obs obs = createBaseObs(conceptUuid);
		String targetUuid = value ? Constants.SIM_UUID : Constants.NAO_UUID;
		obs.setValueCoded(Context.getConceptService().getConceptByUuid(targetUuid));
		encounter.addObs(obs);
		
		return this;
	}
	
	public ObsBuilder addEstadioOmsObs(String estadioOms) {
		if (estadioOms == null || estadioOms.trim().isEmpty()) {
			return this;
		}
		
		Obs obs = createBaseObs(Constants.ESTADIO_OMS_UUID);
		String estadioUuid = mapEstadioToUuid(estadioOms.trim());
		
		if (estadioUuid != null) {
			obs.setValueCoded(Context.getConceptService().getConceptByUuid(estadioUuid));
			encounter.addObs(obs);
		}
		
		return this;
	}
	
	public ObsBuilder addCodedObs(String conceptUuid, String valueConceptUuid) {
		if (conceptUuid == null || valueConceptUuid == null) {
			return this;
		}
		
		Obs obs = createBaseObs(conceptUuid);
		obs.setValueCoded(Context.getConceptService().getConceptByUuid(valueConceptUuid));
		encounter.addObs(obs);
		
		return this;
	}
	
	private Obs createBaseObs(String conceptUuid) {
		Obs obs = new Obs();
		obs.setPerson(patient);
		obs.setEncounter(encounter);
		obs.setConcept(Context.getConceptService().getConceptByUuid(conceptUuid));
		obs.setObsDatetime(encounter.getEncounterDatetime());
		return obs;
	}
	
	public ObsBuilder addTarvRegimeObs(Date dataInicio, Date dataFim, String esquemaTarv) {
		Obs group = createBaseObs(Constants.HISTORIA_TARV_GROUP_UUID);
		
		if (dataInicio != null) {
			Obs inicio = createBaseObs(Constants.TARV_DATA_INICIO_UUID);
			inicio.setValueDatetime(dataInicio);
			group.addGroupMember(inicio);
		}
		
		if (dataFim != null) {
			Obs fim = createBaseObs(Constants.TARV_DATA_FIM_UUID);
			fim.setValueDatetime(dataFim);
			group.addGroupMember(fim);
		}
		
		String esquemaTarvUuid = TarvUtils.mapEsquemaTarvToUuid(esquemaTarv);
		Obs esquemaObs = createBaseObs(Constants.TARV_ESQUEMA_UUID);
		if (esquemaTarvUuid != null) {
			esquemaObs.setValueCoded(Context.getConceptService().getConceptByUuid(esquemaTarvUuid));
		} else {
			esquemaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.OUTRO_UUID));
			
			Obs outroObs = createBaseObs(Constants.OUTRO_TEXT_UUID);
			outroObs.setValueText(esquemaTarv);
			group.addGroupMember(outroObs);
		}
		group.addGroupMember(esquemaObs);
		
		encounter.addObs(group);
		return this;
	}
	
	public ObsBuilder addCd4Obs(Date data, Double absoluto, Double percentual) {
		Obs group = createBaseObs(Constants.HISTORIA_CD4_GROUP_UUID);
		
		if (data != null) {
			Obs dataObs = createBaseObs(Constants.CD4_DATA_UUID);
			dataObs.setValueDatetime(data);
			group.addGroupMember(dataObs);
		}
		
		if (absoluto != null && absoluto > 0) {
			Obs absObs = createBaseObs(Constants.CD4_ABSOLUTO_UUID);
			absObs.setValueNumeric(absoluto);
			group.addGroupMember(absObs);
		}
		
		if (percentual != null && percentual > 0) {
			Obs percObs = createBaseObs(Constants.CD4_PERCENTUAL_UUID);
			percObs.setValueNumeric(percentual);
			group.addGroupMember(percObs);
		}
		
		encounter.addObs(group);
		return this;
	}
	
	public ObsBuilder addCargaViralObs(Date data, Double valor) {
		Obs group = createBaseObs(Constants.HISTORIA_CARGA_VIRAL_GROUP_UUID);
		
		if (data != null) {
			Obs dataObs = createBaseObs(Constants.CV_DATA_UUID);
			dataObs.setValueDatetime(data);
			group.addGroupMember(dataObs);
		}
		
		if (valor != null && valor > 0) {
			Obs valorObs = createBaseObs(Constants.CV_VALOR_UUID);
			valorObs.setValueNumeric(valor);
			group.addGroupMember(valorObs);
		}
		
		encounter.addObs(group);
		return this;
	}
	
	public ObsBuilder addLinhaSolicitadaObs(String linha) {
		if (linha == null || linha.trim().isEmpty()) {
			return this;
		}
		
		Obs group = createBaseObs(Constants.LINHA_SOLICITADA_GROUP_UUID);
		Obs linhaObs = createBaseObs(Constants.LINHA_SOLICITADA_UUID);
		
		String normalized = StringHelper.removeAcentos(linha.trim()).toUpperCase();
		
		if (normalized.contains("2") || normalized.contains("SEGUNDA")) {
			linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.SEGUNDA_LINHA_UUID));
		} else if (normalized.contains("3") || normalized.contains("TERCEIRA")) {
			linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.TERCEIRA_LINHA_UUID));
		} else if (normalized.contains("REGIME") || normalized.contains("INDIVIDUALIZADO")) {
			linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.REGIME_INDIVIDUALIZADO_UUID));
		} else {
			log.warn("Linha solicitada desconhecida: {}", linha);
			return this;
		}
		
		group.addGroupMember(linhaObs);
		encounter.addObs(group);
		return this;
	}
	
	public ObsBuilder addRespostaComiteObs(String groupUuid, String estado, String linha, String comentario,
	        Date dataResposta, String autor, String esquemaAprovado) {
		
		Obs group = new Obs();
		group.setConcept(Context.getConceptService().getConceptByUuid(groupUuid));
		group.setObsDatetime(dataResposta != null ? dataResposta : new Date());
		group.setPerson(encounter.getPatient());
		group.setEncounter(encounter);
		
		if (estado != null) {
			Obs estadoObs = createBaseObs(Constants.RESPOSTA_ESTADO_UUID);
			String normalizedEstado = StringHelper.removeAcentos(estado.trim()).toUpperCase();
			
			if (normalizedEstado.contains("SEM")) {
				estadoObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_SEM_RESPOSTA_UUID));
			} else if (normalizedEstado.contains("APROVADO")) {
				estadoObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_APROVADO_UUID));
			} else if (normalizedEstado.contains("ADIADO")) {
				estadoObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_ADIADO_UUID));
			} else {
				log.warn("Estado do comité desconhecido: {}", estado);
			}
			
			group.addGroupMember(estadoObs);
		}
		
		if (linha != null) {
			Obs linhaObs = createBaseObs(Constants.RESPOSTA_LINHA_UUID);
			String normalizedLinha = StringHelper.removeAcentos(linha.trim()).toUpperCase();
			
			if (normalizedLinha.contains("2") || normalizedLinha.contains("SEGUNDA")) {
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.SEGUNDA_LINHA_UUID));
			} else if (normalizedLinha.contains("3") || normalizedLinha.contains("TERCEIRA")) {
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.TERCEIRA_LINHA_UUID));
			} else if (normalizedLinha.contains("REGIME") || normalizedLinha.contains("INDIVIDUALIZADO")) {
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.REGIME_INDIVIDUALIZADO_UUID));
			} else {
				log.warn("Linha terapêutica da resposta do comité desconhecida: {}", linha);
			}
			
			group.addGroupMember(linhaObs);
		}
		
		// Comentario + Data da Resposta
		if ((comentario != null && !comentario.trim().isEmpty()) || dataResposta != null) {
			
			Obs comentarioObs = createBaseObs(Constants.RESPOSTA_COMENTARIO_UUID);
			
			StringBuilder valor = new StringBuilder();
			
			// comentário primeiro
			if (comentario != null && !comentario.trim().isEmpty()) {
				valor.append(comentario.trim());
			}
			
			if (esquemaAprovado != null && !esquemaAprovado.trim().isEmpty()) {
				if (valor.length() > 0)
					valor.append("  ");
				valor.append("Esquema: ").append(esquemaAprovado.trim());
			}
			
			comentarioObs.setValueText(valor.toString());
			comentarioObs.setObsDatetime(dataResposta != null ? dataResposta : new Date());
			group.addGroupMember(comentarioObs);
		}
		
		if (autor != null && !autor.trim().isEmpty()) {
			Obs autorObs = createBaseObs(Constants.RESPOSTA_AUTOR_UUID);
			autorObs.setValueText(autor.trim());
			group.addGroupMember(autorObs);
		}
		
		encounter.addObs(group);
		return this;
	}
	
	private String mapEstadioToUuid(String estadioOms) {
		if (estadioOms == null || estadioOms.trim().isEmpty()) {
			return null;
		}
		
		// Normaliza o texto: remove acentos, underscores, hífens e espaços extras
		String normalized = StringHelper.removeAcentos(estadioOms).replace("_", "").replace("-", "").replace(" ", "").trim()
		        .toUpperCase();
		
		if (normalized.equals("ESTADIOI")) {
			return Constants.ESTADIO_I_UUID;
		} else if (normalized.equals("ESTADIOII")) {
			return Constants.ESTADIO_II_UUID;
		} else if (normalized.equals("ESTADIOIII")) {
			return Constants.ESTADIO_III_UUID;
		} else if (normalized.equals("ESTADIOIV")) {
			return Constants.ESTADIO_IV_UUID;
		} else {
			log.warn("Estádio OMS desconhecido: {}", estadioOms);
			return null;
		}
	}
	
}
