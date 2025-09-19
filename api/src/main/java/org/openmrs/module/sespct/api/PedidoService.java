package org.openmrs.module.sespct.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.Pedido;
import org.openmrs.module.sespct.api.model.Resposta;

public interface PedidoService extends OpenmrsService {
	
	Pedido savePedido(Pedido pedido);
	
	Resposta saveResposta(Resposta resposta);
	
	Pedido getPedidoById(Integer id);
	
	Pedido getPedidoByExternalId(String externalId);
	
	List<Pedido> getAllPedidos();
	
	List<Pedido> getPedidosByEstado(List<String> estado);
	
	List<Pedido> getPedidosByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime);
	
	void deletePedido(Pedido pedido);
	
	Patient mapIdentifier(String patientUuid, Pedido pedido);
	
	List<Resposta> getRespostasPendentes();
	
	/**
	 * Searches for Pedidos based on a set of filter criteria.
	 * 
	 * @param startDate the start of the submission date range (date only)
	 * @param endDate the end of the submission date range (date only)
	 * @param estado the status of the request
	 * @param ncft the NCFT value
	 * @param nid the NID value
	 * @param usCode the health facility code
	 * @return a list of matching Pedidos
	 */
	List<Pedido> searchPedidos(LocalDate startDate, LocalDate endDate, String estado, String ncft, String nid, String usCode);
	
	/**
	 * Runs the full synchronization process with the central middleware.
	 */
	
	void synchronizeMiddlewareData();
}
