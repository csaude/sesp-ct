package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PedidoService extends OpenmrsService {
	
	void initializeModule();
	
	Pedido savePedido(Pedido pedido);
	
	@Transactional(readOnly = true)
	Pedido getPedidoById(Integer id);
	
	@Transactional(readOnly = true)
	Pedido getPedidoByExternalId(String externalId);
	
	@Transactional(readOnly = true)
	List<Pedido> getAllPedidos();
	
	@Transactional(readOnly = true)
	List<Pedido> getPedidosByEstado(String estado);
	
	void deletePedido(Pedido pedido);
	
	void createDummyData();
	
	void fetchAndUpsertFromCtAsync(String requestId, String facilityCode);
}
