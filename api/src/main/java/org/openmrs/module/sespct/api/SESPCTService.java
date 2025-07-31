package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sespct.api.model.SESPCTRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SESPCTService extends OpenmrsService {
	
	/**
	 * Initialize the module - create tables and populate dummy data
	 */
	void initializeModule();
	
	/**
	 * Save or update a SESP request
	 * 
	 * @param SESPCTRequest the request to save
	 * @return the saved request
	 */
	SESPCTRequest saveSESPCTRequest(SESPCTRequest SESPCTRequest);
	
	/**
	 * Get a SESP request by ID
	 * 
	 * @param id the request ID
	 * @return the request or null if not found
	 */
	@Transactional(readOnly = true)
	SESPCTRequest getSESPCTRequestById(Integer id);
	
	/**
	 * Get a SESP request by pedido ID
	 * 
	 * @param pedidoId the pedido ID
	 * @return the request or null if not found
	 */
	@Transactional(readOnly = true)
	SESPCTRequest getSESPCTRequestByPedidoId(String pedidoId);
	
	/**
	 * Get all SESP requests
	 * 
	 * @return list of all requests
	 */
	@Transactional(readOnly = true)
	List<SESPCTRequest> getAllSESPCTRequests();
	
	/**
	 * Get SESP requests by estado (status)
	 * 
	 * @param estado the status to filter by
	 * @return list of requests with the specified status
	 */
	@Transactional(readOnly = true)
	List<SESPCTRequest> getSESPCTRequestsByEstado(String estado);
	
	/**
	 * Delete a SESP request
	 * 
	 * @param SESPCTRequest the request to delete
	 */
	void deleteSESPCTRequest(SESPCTRequest SESPCTRequest);
	
	/**
	 * Create dummy data for testing
	 */
	void createDummyData();
}
