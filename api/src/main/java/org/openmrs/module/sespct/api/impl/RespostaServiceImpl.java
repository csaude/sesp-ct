package org.openmrs.module.sespct.api.impl;

import java.util.List;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sespct.api.RespostaService;
import org.openmrs.module.sespct.api.dao.RespostaDao;
import org.openmrs.module.sespct.api.model.Resposta;

public class RespostaServiceImpl extends BaseOpenmrsService implements RespostaService {
	
	private RespostaDao respostaDao;
	
	public void setRespostaDao(RespostaDao respostaDao) {
		this.respostaDao = respostaDao;
	}
	
	@Override
	public List<Resposta> getRespostasPendentes() {
		return respostaDao.getRespostasPendentes();
	}
	
	@Override
	public Resposta saveResposta(Resposta resposta) {
		return respostaDao.saveResposta(resposta);
	}
}
