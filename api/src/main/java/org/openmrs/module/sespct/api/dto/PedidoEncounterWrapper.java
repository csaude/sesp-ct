package org.openmrs.module.sespct.api.dto;

import org.openmrs.Encounter;
import org.openmrs.module.sespct.api.model.Pedido;

/**
 * A wrapper class to hold a Pedido and its associated Encounter. This is used to easily pass
 * combined data to the view layer (JSP).
 */
public class PedidoEncounterWrapper {
	
	private Pedido pedido;
	
	private Encounter encounter;
	
	public PedidoEncounterWrapper(Pedido pedido, Encounter encounter) {
		this.pedido = pedido;
		this.encounter = encounter;
	}
	
	// Getters and Setters
	public Pedido getPedido() {
		return pedido;
	}
	
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
}
