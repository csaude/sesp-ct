package org.openmrs.module.sespct.api;

import org.openmrs.module.sespct.api.model.Pedido;

import java.io.IOException;
import java.util.List;

public interface ExportService {
	
	/**
	 * Generates a report from a list of Pedido objects.
	 * 
	 * @return A byte array representing the generated file.
	 */
	byte[] generatePedidoReport(List<Pedido> pedidos, String startDate, String endDate) throws IOException;
}
