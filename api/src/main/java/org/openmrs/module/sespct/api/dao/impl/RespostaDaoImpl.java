package org.openmrs.module.sespct.api.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sespct.api.dao.RespostaDao;
import org.openmrs.module.sespct.api.model.Resposta;

public class RespostaDaoImpl implements RespostaDao {
	
	private DbSessionFactory dbSessionFactory;
	
	public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}
	
	private DbSession getCurrentSession() {
		return dbSessionFactory.getCurrentSession();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Resposta> getRespostasPendentes() {
		final String hql = "from Resposta r where r.sincronizado = false";
		Query query = getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@Override
	public Resposta saveResposta(Resposta resposta) {
		getCurrentSession().saveOrUpdate(resposta);
		return resposta;
	}
}
