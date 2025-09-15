package org.openmrs.module.sespct.api.dao;

import org.openmrs.module.sespct.api.model.Resposta;

import java.util.List;

public interface RespostaDao {
	
	List<Resposta> getRespostasPendentes();
	
	Resposta saveResposta(Resposta resposta);
}
