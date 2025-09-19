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
		
		switch (StringHelper.removeAcentos(linha.trim()).toUpperCase()) {
			case "2 LINHA":
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.SEGUNDA_LINHA_UUID));
				break;
			case "3 LINHA":
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.TERCEIRA_LINHA_UUID));
				break;
			case "REGIME INDIVIDUALIZADO":
				linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.REGIME_INDIVIDUALIZADO_UUID));
				break;
			default:
				log.warn("Linha solicitada desconhecida: {}", linha);
				return this;
		}
		
		group.addGroupMember(linhaObs);
		encounter.addObs(group);
		
		return this;
	}
	
	public ObsBuilder addRespostaComiteObs(String estado, String linha, String comentario, Date dataResposta, String autor) {
		Obs group = createBaseObs(Constants.RESPOSTA_COMITE_GROUP_UUID);
		
		if (estado != null) {
			Obs estadoObs = createBaseObs(Constants.RESPOSTA_ESTADO_UUID);
			switch (StringHelper.removeAcentos(estado.trim()).toUpperCase()) {
				case "SEM_RESPOSTA":
					estadoObs
					        .setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_SEM_RESPOSTA_UUID));
					break;
				case "APROVADO":
					estadoObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_APROVADO_UUID));
					break;
				case "ADIADO":
					estadoObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.ESTADO_ADIADO_UUID));
					break;
				default:
					log.warn("Estado do comité desconhecido: {}", estado);
			}
			group.addGroupMember(estadoObs);
		}
		
		if (linha != null) {
			Obs linhaObs = createBaseObs(Constants.RESPOSTA_LINHA_UUID);
			switch (StringHelper.removeAcentos(linha.trim()).toUpperCase()) {
				case "2 LINHA":
					linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.SEGUNDA_LINHA_UUID));
					break;
				case "3 LINHA":
					linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(Constants.TERCEIRA_LINHA_UUID));
					break;
				case "REGIME INDIVIDUALIZADO":
					linhaObs.setValueCoded(Context.getConceptService().getConceptByUuid(
					    Constants.REGIME_INDIVIDUALIZADO_UUID));
					break;
				default:
					log.warn("Linha terapêutica da resposta do comité desconhecida: {}", linha);
			}
			group.addGroupMember(linhaObs);
		}
		
		// Comentario + Data da Resposta
		if ((comentario != null && !comentario.trim().isEmpty()) || dataResposta != null) {
			Obs comentarioObs = createBaseObs(Constants.RESPOSTA_COMENTARIO_UUID);
			if (comentario != null && !comentario.trim().isEmpty()) {
				comentarioObs.setValueText(comentario.trim());
			}
			if (dataResposta != null) {
				comentarioObs.setObsDatetime(dataResposta);
			}
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
		switch (StringHelper.removeAcentos(estadioOms).trim().toUpperCase()) {
			case "ESTADIO I":
				return Constants.ESTADIO_I_UUID;
			case "ESTADIO II":
				return Constants.ESTADIO_II_UUID;
			case "ESTADIO III":
				return Constants.ESTADIO_III_UUID;
			case "ESTADIO IV":
				return Constants.ESTADIO_IV_UUID;
			default:
				log.warn("Estádio OMS desconhecido: {}", estadioOms);
				return null;
		}
	}
}
