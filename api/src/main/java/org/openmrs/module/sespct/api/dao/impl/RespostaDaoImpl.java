package org.openmrs.module.sespct.api.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.module.sespct.api.dao.RespostaDao;
import org.openmrs.module.sespct.api.model.Resposta;

public class RespostaDaoImpl implements RespostaDao {
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Resposta> getRespostasPendentes() {
		String hql = "from Resposta r where r.sincronizado = false";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
	
	@Override
	public Resposta saveResposta(Resposta resposta) {
		sessionFactory.getCurrentSession().saveOrUpdate(resposta);
		return resposta;
	}
}
