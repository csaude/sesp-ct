package org.openmrs.module.sespct.api.util;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;
import org.openmrs.module.sespct.api.model.RespostaComite;

public class RespostaUtils {
	
	/**
	 * Função auxiliar para extrair timestamp de forma segura.
	 */
	private static LocalDateTime getTimestampSafe(Resposta r) {
		return (r != null && r.getMetadados() != null) ? r.getMetadados().getTimestamp() : null;
	}
	
	/**
	 * Retorna os dois últimos registos de RespostaComite de um Pedido. O primeiro elemento da lista
	 * é o mais recente, o segundo é o histórico anterior.
	 * 
	 * @param pedido Pedido associado
	 * @return Lista com até 2 RespostaComite (mais recente e anterior)
	 */
	public static List<RespostaComite> getUltimasDuasRespostas(Pedido pedido) {
        if (pedido == null || pedido.getRespostas() == null) {
            return java.util.Collections.emptyList();
        }

        return pedido.getRespostas().stream()
                .filter(r -> r.getRespostaComite() != null)
                .sorted(Comparator.comparing(
                        RespostaUtils::getTimestampSafe,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()) // mais recentes primeiro
                .map(Resposta::getRespostaComite)
                .limit(2) // pega só os dois últimos
                .collect(Collectors.toList());
    }
}
