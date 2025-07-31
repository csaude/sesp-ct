package org.openmrs.module.sespct.api.dao;

import org.openmrs.module.sespct.api.model.SESPCTRequest;

import java.util.List;

public interface SESPCTDao {
	
	/**
	 * Save or update a SESP request
	 * 
	 * @param sespRequest the request to save
	 * @return the saved request
	 */
	SESPCTRequest saveSESPCTRequest(SESPCTRequest sespRequest);
	
	/**
	 * Get a SESP request by ID
	 * 
	 * @param id the request ID
	 * @return the request or null if not found
	 */
	SESPCTRequest getSESPCTRequestById(Integer id);
	
	/**
	 * Get a SESP request by pedido ID
	 * 
	 * @param pedidoId the pedido ID
	 * @return the request or null if not found
	 */
	SESPCTRequest getSESPCTRequestByPedidoId(String pedidoId);
	
	/**
	 * Get all SESP requests
	 * 
	 * @return list of all requests
	 */
	List<SESPCTRequest> getAllSESPCTRequests();
	
	/**
	 * Get SESP requests by estado (status)
	 * 
	 * @param estado the status to filter by
	 * @return list of requests with the specified status
	 */
	List<SESPCTRequest> getSESPCTRequestsByEstado(String estado);
	
	/**
	 * Delete a SESP request
	 * 
	 * @param sespRequest the request to delete
	 */
	void deleteSESPCTRequest(SESPCTRequest sespRequest);
}
