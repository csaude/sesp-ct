package org.openmrs.module.sespct.api.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;

public class RespostaUtils {
	
	public static List<Resposta> getUltimasDuasRespostas(Pedido pedido) {
	    if (pedido == null || pedido.getRespostas() == null) {
	        return Collections.emptyList();
	    }

	    return pedido.getRespostas().stream()
	        .sorted(Comparator.comparing(Resposta::getDataResposta,
	                Comparator.nullsLast(Comparator.naturalOrder())).reversed())
	        .limit(2)
	        .collect(Collectors.toList());
	}
}
