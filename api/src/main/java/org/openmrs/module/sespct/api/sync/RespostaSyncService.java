package org.openmrs.module.sespct.api.sync;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.model.RespostaComite;
import org.openmrs.module.sespct.api.util.EncounterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSyncService {
	
    private static final Logger log = LoggerFactory.getLogger(RespostaSyncService.class);

    /**
     * Atualiza o Encounter já existente de um Pedido com os dados da Resposta do Comité
     */
    public void updateEncounterWithResposta(Pedido pedido, Resposta resposta) {
        Encounter encounter = EncounterUtils.findEncounterByPedidoId(pedido.getPedidoId());

        if (encounter == null) {
            log.error("No Encounter found for Pedido id={}", pedido.getPedidoId());
            return;
        }

        ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());
        RespostaComite rc = resposta.getRespostaComite();

        obsBuilder.addRespostaComiteObs(
                rc.getRespostaTexto(),     // Estado: APROVADO / ADIADO
                rc.getLinhaTerapeutica(),  // Linha terapêutica
                rc.getComentario(),        // Comentário
                rc.getDataResposta() != null ? java.sql.Timestamp.valueOf(rc.getDataResposta()) : null, // Data resposta
                rc.getAutorizante()        // Quem autorizou
        );

        Context.getEncounterService().saveEncounter(encounter);
        log.info("Encounter atualizado com Resposta id={} para Pedido id={}", resposta.getId(), pedido.getPedidoId());
    }
}
