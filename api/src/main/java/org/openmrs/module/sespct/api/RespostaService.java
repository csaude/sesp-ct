package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Resposta;

import java.util.List;

public interface RespostaService extends OpenmrsService {
    
    /**
     * Buscar todas as respostas pendentes de sincronização.
     * Ex: estado "NOVO" ou "NAO_SINCRONIZADO".
     */
    List<Resposta> getRespostasPendentes();

    /**
     * Salvar ou atualizar uma resposta.
     */
    Resposta saveResposta(Resposta resposta);
}
