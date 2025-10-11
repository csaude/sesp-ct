package org.openmrs.module.sespct.api.sync;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSyncService {
	
	private static final Logger log = LoggerFactory.getLogger(RespostaSyncService.class);
	
	/**
	 * Atualiza um Encounter com até duas respostas do comité (mais recente + histórico).
	 */
	public void updateEncounterWithRespostas(Pedido pedido, Encounter encounter, List<Resposta> respostas) {
		if (respostas == null || respostas.isEmpty()) {
			log.warn("Nenhuma resposta do comité encontrada para Pedido id={}", pedido.getPedidoId());
			return;
		}
		
		ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());
		
		// Processar respostas de acordo com a posição
		for (int i = 0; i < respostas.size() && i < 2; i++) {
			Resposta resposta = respostas.get(i);
			
			// Determinar qual grupo usar
			String groupUuid;
			String tipoResposta;
			
			if (i == 0) {
				// Primeira resposta (mais recente) → Grupo principal
				groupUuid = Constants.RESPOSTA_COMITE_GROUP_UUID;
				tipoResposta = "atual";
			} else {
				// Segunda resposta (anterior) → Grupo histórico
				groupUuid = Constants.RESPOSTA_HISTORICO_GROUP_UUID;
				tipoResposta = "histórico";
			}
			
			log.debug("Adicionando resposta {} para Pedido id={} no grupo {}", tipoResposta, pedido.getPedidoId(), groupUuid);
			
			// Converter data para java.util.Date se necessário
			java.util.Date dataResposta = null;
			if (resposta.getDataResposta() != null) {
				dataResposta = java.sql.Timestamp.valueOf(resposta.getDataResposta());
			}
			
			// Adicionar resposta ao grupo correto
			obsBuilder.addRespostaComiteObs(groupUuid, // UUID do grupo (atual ou histórico)
			    resposta.getResposta(), // Estado (Aprovado/Adiado/Sem resposta)
			    resposta.getLinhaTerapeutica(), // Linha terapêutica
			    resposta.getComentario(), // Comentário
			    dataResposta, // Data da resposta
			    resposta.getAutorizante() // Autor
			        );
		}
		
		Context.getEncounterService().saveEncounter(encounter);
		log.info("Encounter do Pedido id={} atualizado com {} respostas do comité", pedido.getPedidoId(), respostas.size());
	}
}
