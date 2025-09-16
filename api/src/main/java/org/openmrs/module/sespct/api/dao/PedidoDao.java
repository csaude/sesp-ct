package org.openmrs.module.sespct.api.dao;

import org.openmrs.module.sespct.api.model.Pedido;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PedidoDao {
	
	Pedido savePedido(Pedido pedido);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByIdAndStatus(Integer id, String estado);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(String estado);
	
	void deletePedido(Pedido pedido);
	
	List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
