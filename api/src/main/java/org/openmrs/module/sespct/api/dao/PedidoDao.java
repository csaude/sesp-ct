package org.openmrs.module.sespct.api.dao;

import org.openmrs.module.sespct.api.model.Pedido;

import java.util.Date;
import java.util.List;

public interface PedidoDao {
	
	Pedido savePedido(Pedido pedido);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(String estado);
	
	void deletePedido(Pedido pedido);
	
	List<Pedido> getPedidosByDateRange(Date startDate, Date endDate);
}
