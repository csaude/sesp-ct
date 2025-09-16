package org.openmrs.module.sespct.api.sync;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.sespct.api.RespostaService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.model.RespostaComite;
import org.openmrs.module.sespct.api.util.EncounterUtils;
import org.openmrs.module.sespct.api.util.RespostaUtils;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scheduled task para processar respostas do Comité Terapêutico e atualizar os respectivos
 * Encounters.
 */
public class RespostaSchedulerTask extends AbstractTask {
	
	private static final Logger log = LoggerFactory.getLogger(RespostaSchedulerTask.class);
	
	private RespostaService respostaService;
	
	private RespostaSyncService respostaSyncService;
	
	@Override
	public void execute() {
		log.info("Iniciando RespostaSchedulerTask...");
		
		try {
			respostaService = Context.getService(RespostaService.class);
			respostaSyncService = new RespostaSyncService();
			
			processRespostas();
			
		}
		catch (Exception e) {
			log.error("Erro inesperado na execução do RespostaSchedulerTask", e);
		}
	}
	
	private void processRespostas() {
		List<Resposta> respostasPendentes = respostaService.getRespostasPendentes();
		log.info("Foram encontradas {} respostas pendentes", respostasPendentes.size());
		
		for (Resposta resposta : respostasPendentes) {
			Pedido pedido = resposta.getPedido();
			
			if (pedido == null) {
				log.error("Resposta id={} não tem Pedido associado!", resposta.getId());
				continue;
			}
			
			Encounter encounter = EncounterUtils.findEncounterByPedidoId(pedido.getPedidoId());
			
			if (encounter == null) {
				// Encounter ainda não foi criado → Pedido ainda não processado
				log.warn(
				    "Encounter não encontrado para Pedido id={} (Resposta id={}), será tentado novamente no próximo ciclo",
				    pedido.getPedidoId(), resposta.getId());
				
				//resposta.setSincronizado(false); // mantém como pendente
				respostaService.saveResposta(resposta);
				continue;
			}
			
			try {
				// Recupera até 2 últimas respostas do comité
				List<RespostaComite> ultimasRespostas = RespostaUtils.getUltimasDuasRespostas(pedido);
				
				if (ultimasRespostas.isEmpty()) {
					log.warn("Nenhuma RespostaComite válida encontrada para Resposta id={} (Pedido id={})",
					    resposta.getId(), pedido.getPedidoId());
					continue;
				}
				
				respostaSyncService.updateEncounterWithRespostas(pedido, encounter, ultimasRespostas);
				
				// Marca como sincronizado
				//resposta.setSincronizado(true);
				respostaService.saveResposta(resposta);
				
				log.info("Resposta id={} aplicada com sucesso ao Encounter do Pedido id={}", resposta.getId(),
				    pedido.getPedidoId());
				
			}
			catch (Exception e) {
				log.error("Erro ao aplicar Resposta id={} ao Pedido id={}", resposta.getId(), pedido.getPedidoId(), e);
				//resposta.setSincronizado(false); // garante retry
				respostaService.saveResposta(resposta);
			}
		}
	}
}
