package org.openmrs.module.sespct.api.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Query;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.sespct.api.dao.SESPCTDao;
import org.openmrs.module.sespct.api.model.SESPCTRequest;

import java.util.List;

public class SESPCTDaoImpl extends HibernateOpenmrsDataDAO<SESPCTRequest> implements SESPCTDao {
	
	private static final Log log = LogFactory.getLog(SESPCTDaoImpl.class);
	
	public SESPCTDaoImpl() {
		super(SESPCTRequest.class);
	}
	
	/**
	 * Get the current Hibernate session
	 */
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public SESPCTRequest saveSESPCTRequest(SESPCTRequest sespRequest) {
		return saveOrUpdate(sespRequest);
	}
	
	@Override
	public SESPCTRequest getSESPCTRequestById(Integer id) {
		return getById(id);
	}
	
	@Override
	public SESPCTRequest getSESPCTRequestByPedidoId(String pedidoId) {
		Query query = getSession().createQuery("FROM SESPCTRequest WHERE pedidoId = :pedidoId");
		query.setParameter("pedidoId", pedidoId);
		return (SESPCTRequest) query.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<SESPCTRequest> getAllSESPCTRequests() {
		Query query = getSession().createQuery("FROM SESPCTRequest ORDER BY dateCreated DESC");
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<SESPCTRequest> getSESPCTRequestsByEstado(String estado) {
		Query query = getSession().createQuery("FROM SESPCTRequest WHERE estado = :estado ORDER BY dateCreated DESC");
		query.setParameter("estado", estado);
		return query.list();
	}
	
	@Override
	public void deleteSESPCTRequest(SESPCTRequest sespRequest) {
		delete(sespRequest);
	}
}
