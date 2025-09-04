package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Pedido;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PedidoService extends OpenmrsService {
	
	Pedido savePedido(Pedido pedido);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(String estado);
	
	List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
	
	void deletePedido(Pedido pedido);
	
	void createDummyData();
	
}
