package org.openmrs.module.sespct.api.sync;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.model.RespostaComite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSyncService {
	
    private static final Logger log = LoggerFactory.getLogger(RespostaSyncService.class);
    
    private Encounter findEncounterByPedidoId(String pedidoId) {
        // Reutiliza o método que já tens em SespctSchedulerTask,
        // ou move esse método para um utilitário comum
        return null; // TODO: implementar
    }
    
    public void updateEncounterWithResposta(Pedido pedido, Resposta resposta) {
    	Encounter encounter = findEncounterByPedidoId(pedido.getPedidoId());
    	
    	if (encounter == null) {
            log.error("No Encounter found for Pedido id={}", pedido.getPedidoId());
            return;
        }
    	
    	ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());
    	
    	RespostaComite rc = resposta.getRespostaComite();
    	
    	obsBuilder.addRespostaComiteObs(
                rc.getRespostaTexto(),    // "APROVADO" / "ADIADO"
                rc.getLinhaTerapeutica(), // Segunda / Terceira / Individualizado
                rc.getComentario(),
                rc.getDataResposta() != null ? java.sql.Timestamp.valueOf(rc.getDataResposta()) : null,
                rc.getAutorizante()
            );
    	
    	Context.getEncounterService().saveEncounter(encounter);
        log.info("Encounter atualizado com Resposta id={} para Pedido id={}", resposta.getId(), pedido.getPedidoId());
    }
}
