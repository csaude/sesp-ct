package org.openmrs.module.sespct.api.dao;

import org.openmrs.module.sespct.api.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoDao {
	
	Pedido savePedido(Pedido pedido);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByIdAndStatus(Integer id, String estado);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(List<String> estados);
	
	void deletePedido(Pedido pedido);
	
	List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
	
	/**
	 * Searches for Pedidos based on a set of filter criteria.
	 * 
	 * @param startDateTime the start of the submission date range
	 * @param endDateTime the end of the submission date range
	 * @param estado the status of the request
	 * @param ncft the NCFT value
	 * @param nid the NID value
	 * @param usCode the health facility code (origem)
	 * @return a list of matching Pedidos
	 */
	List<Pedido> searchPedidos(LocalDateTime startDateTime, LocalDateTime endDateTime, String estado, String ncft,
	        String nid, String usCode);
	
}
