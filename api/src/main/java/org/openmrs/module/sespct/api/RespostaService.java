package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Resposta;

import java.util.List;

public interface RespostaService extends OpenmrsService {
	
	List<Resposta> getRespostasPendentes();
	
	Resposta saveResposta(Resposta resposta);
}
