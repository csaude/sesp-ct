package org.openmrs.module.sespct.api.sync;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.util.EncounterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSyncService {
	//
	//	private static final Logger log = LoggerFactory.getLogger(RespostaSyncService.class);
	//
	//	/**
	//	 * Atualiza um Encounter com apenas uma resposta do comité (caso legacy).
	//	 */
	//	public void updateEncounterWithResposta(Pedido pedido, Resposta resposta) {
	//		Encounter encounter = EncounterUtils.findEncounterByPedidoId(pedido.getPedidoId());
	//
	//		if (encounter == null) {
	//			log.error("No Encounter found for Pedido id={}", pedido.getPedidoId());
	//			return;
	//		}
	//
	//		ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());
	//		RespostaComite rc = resposta.getRespostaComite();
	//
	//		obsBuilder.addRespostaComiteObs(rc.getRespostaTexto(), rc.getLinhaTerapeutica(), rc.getComentario(),
	//		    rc.getDataResposta() != null ? java.sql.Timestamp.valueOf(rc.getDataResposta()) : null, rc.getAutorizante());
	//
	//		Context.getEncounterService().saveEncounter(encounter);
	//		log.info("Encounter atualizado com Resposta id={} para Pedido id={}", resposta.getId(), pedido.getPedidoId());
	//	}
	//
	//	/**
	//	 * Atualiza um Encounter com até duas respostas do comité (mais recente + histórico).
	//	 */
	//	public void updateEncounterWithRespostas(Pedido pedido, Encounter encounter, List<RespostaComite> respostas) {
	//		if (respostas == null || respostas.isEmpty()) {
	//			log.warn("Nenhuma resposta do comité encontrada para Pedido id={}", pedido.getPedidoId());
	//			return;
	//		}
	//
	//		ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());
	//
	//		for (RespostaComite rc : respostas) {
	//			obsBuilder.addRespostaComiteObs(rc.getRespostaTexto(), rc.getLinhaTerapeutica(), rc.getComentario(),
	//			    rc.getDataResposta() != null ? java.sql.Timestamp.valueOf(rc.getDataResposta()) : null, rc.getAutorizante());
	//		}
	//
	//		Context.getEncounterService().saveEncounter(encounter);
	//		log.info("Encounter do Pedido id={} atualizado com {} respostas do comité", pedido.getPedidoId(), respostas.size());
	//	}
}
