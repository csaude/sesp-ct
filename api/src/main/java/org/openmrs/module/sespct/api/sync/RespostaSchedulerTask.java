package org.openmrs.module.sespct.api.sync;

import java.sql.Timestamp;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.RespostaService;
import org.openmrs.module.sespct.api.builder.ObsBuilder;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.model.RespostaComite;
import org.openmrs.module.sespct.api.util.EncounterUtils;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RespostaSchedulerTask extends AbstractTask {

    private static final Logger log = LoggerFactory.getLogger(RespostaSchedulerTask.class);

    @Override
    public void execute() {
        log.info("Starting RespostaSchedulerTask...");

        try {
            // Pega todas as respostas pendentes (ex: estado "NOVO" ou "NAO_SINCRONIZADO")
            List<Resposta> respostas = Context.getService(RespostaService.class).getRespostasPendentes();

            if (respostas.isEmpty()) {
                log.info("No pending respostas to process.");
                return;
            }

            for (Resposta resposta : respostas) {
                try {
                    processResposta(resposta);
                } catch (Exception e) {
                    log.error("Erro processando Resposta id={}", resposta.getId(), e);
                }
            }
        }
        catch (Exception e) {
            log.error("Erro geral no RespostaSchedulerTask", e);
        }

        log.info("Finished RespostaSchedulerTask.");
    }

    private void processResposta(Resposta resposta) {
        Pedido pedido = resposta.getPedido();
        Encounter encounter = EncounterUtils.findEncounterByPedidoId(pedido.getPedidoId());

        if (encounter == null) {
            log.warn("Nenhum Encounter encontrado para Pedido id={} ao processar Resposta id={}", 
                     pedido.getPedidoId(), resposta.getId());
            return;
        }

        RespostaComite rc = resposta.getRespostaComite();
        if (rc == null) {
            log.warn("Resposta id={} não tem dados do comité", resposta.getId());
            return;
        }

        ObsBuilder obsBuilder = new ObsBuilder(encounter, encounter.getPatient());

        obsBuilder.addRespostaComiteObs(
            rc.getRespostaTexto(), 
            rc.getLinhaTerapeutica(),
            rc.getComentario(),
            rc.getDataResposta() != null ? Timestamp.valueOf(rc.getDataResposta()) : null,
            rc.getAutorizante()
        );

        Context.getEncounterService().saveEncounter(encounter);

        log.info("Encounter atualizado com Resposta id={} para Pedido id={}", resposta.getId(), pedido.getPedidoId());

        // Marca como processada
        resposta.getMetadados().setProcessadoPor("sespct-scheduler");
        Context.getService(RespostaService.class).saveResposta(resposta);
    }
}
