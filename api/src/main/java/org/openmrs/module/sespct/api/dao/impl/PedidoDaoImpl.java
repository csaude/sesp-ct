package org.openmrs.module.sespct.api.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.model.Pedido;

import java.time.LocalDateTime;
import java.util.*;

public class PedidoDaoImpl implements PedidoDao {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DbSessionFactory dbSessionFactory;
	
	public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}
	
	public DbSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}
	
	private DbSession getCurrentSession() {
		return dbSessionFactory.getCurrentSession();
	}
	
	@Override
	public Pedido savePedido(Pedido pedido) {
		this.getCurrentSession().saveOrUpdate(pedido);
		return pedido;
	}
	
	@Override
	public Pedido getPedidoById(Integer id) {
		return (Pedido) this.getCurrentSession().get(Pedido.class, id);
	}
	
	@Override
	public Pedido getPedidoByIdAndStatus(Integer id, String estado) {
		String hql = "FROM Pedido p WHERE p.id = :id AND p.estado = :status";
		final Query query = this.getCurrentSession().createQuery(hql).setParameter("id", id).setParameter("estado", estado);
		
		@SuppressWarnings("unchecked")
		List<Pedido> results = query.list();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public Pedido getPedidoByExternalId(String externalId) {
		final String hql = "FROM Pedido WHERE pedidoExternalId = :externalId AND voided = 0";
		final Query query = this.getCurrentSession().createQuery(hql).setParameter("externalId", externalId);
		
		@SuppressWarnings("unchecked")
		List<Pedido> results = query.list();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> getAllPedidos() {
		final String hql = "FROM Pedido WHERE voided = 0 ORDER BY dateCreated DESC";
		final Query query = this.getCurrentSession().createQuery(hql);
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> getPedidosByEstado(List<String> estados) {
		if (estados == null || estados.isEmpty()) {
			return java.util.Collections.emptyList();
		}
		
		final String hql = "FROM Pedido WHERE estado IN (:estados) AND voided = 0 ORDER BY dateCreated DESC";
		final Query query = this.getCurrentSession().createQuery(hql).setParameterList("estados", estados);
		
		return query.list();
	}
	
	@Override
	public void deletePedido(Pedido pedido) {
		// Don't actually delete, just void it (OpenMRS pattern)
		pedido.setVoided(true);
		this.getCurrentSession().saveOrUpdate(pedido);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		final String hql = "FROM Pedido p WHERE p.dataSubmissao BETWEEN :startDateTime AND :endDateTime "
		        + "AND p.voided = false ORDER BY p.dataSubmissao DESC";
		
		// 1. Use the non-generic org.hibernate.Query
		final Query query = this.getCurrentSession().createQuery(hql).setParameter("startDateTime", startDateTime)
		        .setParameter("endDateTime", endDateTime);
		
		// 2. Use the .list() method, which is correct for Hibernate 4
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Pedido> searchPedidos(LocalDateTime startDateTime, LocalDateTime endDateTime, String estado, String ncft, String nid, String usCode) {
		// Use StringBuilder for efficient string manipulation
		StringBuilder hql = new StringBuilder("FROM Pedido p WHERE p.voided = false ");

		// Use a map to safely add parameters and avoid HQL injection
		Map<String, Object> parameters = new HashMap<>();

		if (startDateTime != null) {
			hql.append("AND p.dataSubmissao >= :startDateTime ");
			parameters.put("startDateTime", startDateTime);
		}

		if (endDateTime != null) {
			hql.append("AND p.dataSubmissao <= :endDateTime ");
			parameters.put("endDateTime", endDateTime);
		}

		// Check for state, ignoring the "ALL" value
		if (estado != null && !estado.trim().isEmpty() && !"ALL".equalsIgnoreCase(estado)) {
			hql.append("AND p.estado = :estado ");
			parameters.put("estado", estado);
		}

		if (ncft != null && !ncft.trim().isEmpty()) {
			hql.append("AND p.pedidoId = :ncft ");
			parameters.put("ncft", ncft);
		}

		if (nid != null && !nid.trim().isEmpty()) {
			hql.append("AND p.dadosUtente.nid = :nid ");
			parameters.put("nid", nid);
		}

		// Check for US code, ignoring the "ALL" value
		if (usCode != null && !usCode.trim().isEmpty() && !"ALL".equalsIgnoreCase(usCode)) {
			// Assuming the 'origem' field holds the US code
			hql.append("AND p.origem = :usCode ");
			parameters.put("usCode", usCode);
		}

		// Always order by submission date descending, as per requirements
		hql.append("ORDER BY p.dataSubmissao DESC");

		// Create the query
		final Query query = this.getCurrentSession().createQuery(hql.toString());

		// Bind all the parameters from our map
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}

		return query.list();
	}
}
