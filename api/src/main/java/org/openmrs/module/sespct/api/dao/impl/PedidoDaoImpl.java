package org.openmrs.module.sespct.api.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.model.Pedido;

import java.util.List;

public class PedidoDaoImpl extends HibernateOpenmrsDataDAO<Pedido> implements PedidoDao {
	
	private static final Log log = LogFactory.getLog(PedidoDaoImpl.class);
	
	public PedidoDaoImpl() {
		super(Pedido.class);
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Pedido savePedido(Pedido pedido) {
		return saveOrUpdate(pedido);
	}
	
	@Override
	public Pedido getPedidoById(Integer id) {
		return getById(id);
	}
	
	@Override
	public Pedido getPedidoByExternalId(String externalId) {
		Query query = getSession().createQuery("FROM Pedido WHERE pedidoExternalId = :externalId");
		query.setParameter("externalId", externalId);
		return (Pedido) query.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> getAllPedidos() {
		Query query = getSession().createQuery("FROM Pedido ORDER BY dateCreated DESC");
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> getPedidosByEstado(String estado) {
		Query query = getSession().createQuery("FROM Pedido WHERE estado = :estado ORDER BY dateCreated DESC");
		query.setParameter("estado", estado);
		return query.list();
	}
	
	@Override
	public void deletePedido(Pedido pedido) {
		delete(pedido);
	}
}
