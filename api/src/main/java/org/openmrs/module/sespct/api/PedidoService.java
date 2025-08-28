package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Pedido;

import java.util.Date;
import java.util.List;

public interface PedidoService extends OpenmrsService {
	
	Pedido savePedido(Pedido pedido);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(String estado);
	
	List<Pedido> getPedidosByDateRange(Date startDate, Date endDate);
	
	void deletePedido(Pedido pedido);
	
	void createDummyData();
	
}
