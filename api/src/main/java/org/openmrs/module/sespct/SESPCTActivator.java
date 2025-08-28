/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.sespct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.module.sespct.api.dao.PedidoDao;
import org.openmrs.module.sespct.api.model.*;

import java.util.*;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class SESPCTActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public void willRefreshContext() {
		log.info("Refreshing SESP-CT Module");
	}
	
	public void contextRefreshed() {
		log.info("SESP-CT Module refreshed");
	}
	
	public void willStart() {
		log.info("Starting SESP-CT Module");
	}
	
	public void started() {
		System.out.println("### Starting SESP-CT Module");
		log.info("SESP-CT Module started");
		try {
			initializeModule();
			log.info("SESP-CT Module initialization completed successfully");
		}
		catch (Exception e) {
			log.error("Error initializing SESP-CT Module", e);
		}
	}
	
	public void willStop() {
		log.info("Stopping SESP-CT Module");
	}
	
	public void stopped() {
		log.info("SESP-CT Module stopped");
	}
	
	private void initializeModule() {
		log.info("Initializing SESP-CT Module...");
		
		try {
			PedidoService pedidoService = Context.getService(PedidoService.class);
			
			List<Pedido> existingPedidos = pedidoService.getAllPedidos();
			if (existingPedidos.isEmpty() || existingPedidos.size() > 20) {
				log.info("No existing data found. Creating dummy data...");
				pedidoService.createDummyData(); // Move createDummyData to service
			} else {
				log.info("Found " + existingPedidos.size() + " existing pedidos. Skipping dummy data creation.");
			}
		}
		catch (Exception e) {
			log.error("Failed to initialize module data", e);
			throw e;
		}
	}
	
}
