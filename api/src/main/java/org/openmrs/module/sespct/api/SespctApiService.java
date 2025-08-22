package org.openmrs.module.sespct.api;

import org.openmrs.api.OpenmrsService;

public interface SespctApiService extends OpenmrsService {
	
	/**
	 * Fetches a single Pedido by its ID from the remote API, decrypts it, and returns the raw
	 * decrypted JSON data.
	 * 
	 * @param externalId The ID of the Pedido on the remote system (e.g., "1001").
	 * @return The decrypted Pedido as a JSON String, or null if an error occurs.
	 */
	String fetchPedidoById(String externalId);
	
	/**
	 * A high-level method to synchronize data from the API. This will be called by our scheduled
	 * task.
	 */
	void syncPedidosFromApi();
}
