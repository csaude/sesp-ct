package org.openmrs.module.sespct.api.sync;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSyncService {
	
	private static final Logger log = LoggerFactory.getLogger(RespostaSyncService.class);
	
	public void updateEncounterWithRespostas(Pedido pedido, Encounter encounter, List<Resposta> respostas) {
	    if (respostas == null || respostas.isEmpty()) {
	        log.warn("Nenhuma resposta do comité encontrada para Pedido id={}", pedido.getPedidoId());
	        return;
	    }

	    ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());

	    removeExistingGroup(encounter, Constants.RESPOSTA_COMITE_GROUP_UUID);

	    respostas.sort(Comparator.comparing(Resposta::getDataResposta, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

	    for (int i = 0; i < respostas.size() && i < 2; i++) {
	        Resposta resposta = respostas.get(i);
	        String groupUuid = (i == 0)
	            ? Constants.RESPOSTA_COMITE_GROUP_UUID // grupo atual
	            : Constants.RESPOSTA_HISTORICO_GROUP_UUID; // grupo histórico

	        java.util.Date dataResposta = null;
	        if (resposta.getDataResposta() != null) {
	            dataResposta = java.sql.Timestamp.valueOf(resposta.getDataResposta());
	        }

	        obsBuilder.addRespostaComiteObs(
	            groupUuid,
	            resposta.getResposta(),
	            resposta.getLinhaTerapeutica(),
	            resposta.getComentario(),
	            dataResposta,
	            resposta.getAutorizante(),
	            resposta.getEsquemaAprovado()
	        );

	        log.debug("Adicionada resposta {} ao grupo {}", (i == 0 ? "atual" : "histórica"), groupUuid);
	    }

	    Context.getEncounterService().saveEncounter(encounter);
	    log.info("Encounter do Pedido id={} atualizado com {} respostas (1 atual + 1 histórico)",
	        pedido.getPedidoId(), respostas.size());
	}
	
	/**
	 * Remove o grupo de observações existente com o concept UUID indicado (para evitar duplicação).
	 */
	private void removeExistingGroup(Encounter encounter, String groupConceptUuid) {
	    if (encounter.getAllObs() == null || encounter.getAllObs().isEmpty()) {
	        return;
	    }

	    List<Obs> toRemove = encounter.getAllObs().stream()
	        .filter(obs -> obs.isObsGrouping() &&
	            obs.getConcept().getUuid().equals(groupConceptUuid) &&
	            !obs.getVoided())
	        .collect(Collectors.toList());

	    for (Obs group : toRemove) {
	        group.setVoided(true);
	        group.setVoidReason("Atualização automática da resposta mais recente do comité");
	    }

	    if (!toRemove.isEmpty()) {
	        log.info("Removido {} grupo(s) existentes com UUID={} antes de adicionar nova resposta",
	            toRemove.size(), groupConceptUuid);
	    }
	}
}
