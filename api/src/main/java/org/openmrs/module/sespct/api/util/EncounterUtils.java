package org.openmrs.module.sespct.api.util;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class EncounterUtils {

    private static final Logger log = LoggerFactory.getLogger(EncounterUtils.class);

    private EncounterUtils() {
        // Utility class, evita instanciar
    }
    
    public static Encounter findEncounterByPedidoId(String pedidoId) {
    	if (pedidoId == null || pedidoId.trim().isEmpty()) {
            log.warn("PedidoId is null or empty in findEncounterByPedidoId()");
            return null;
        }
    	
    	String hql = "select o.encounter " +
                "from Obs o " +
                "where o.concept.uuid = :conceptUuid " +
                "and o.valueNumeric = :pedidoId";

    	SessionFactory sessionFactory = Context.getRegisteredComponent("sessionFactory", SessionFactory.class);
    	Session session = sessionFactory.getCurrentSession();

    	@SuppressWarnings("unchecked")
		List<Encounter> encounters = session.createQuery(hql)
    	    .setParameter("conceptUuid", Constants.ID_PEDIDO_UUID)
    	    .setParameter("pedidoId", Double.valueOf(pedidoId))
    	    .list();
	   
	   if (encounters.isEmpty()) {
           log.info("No encounter found for PedidoId={}", pedidoId);
           return null;
       }
	   
	   if (encounters.size() > 1) {
           log.warn("Multiple encounters found for PedidoId={}. Returning the first.", pedidoId);
       }

       return encounters.get(0);
    }
}
