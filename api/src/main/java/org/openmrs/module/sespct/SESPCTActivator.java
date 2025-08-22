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
import org.openmrs.module.sespct.api.PedidoService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.sespct.api.SespctApiService;

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
			// We are testing the NEW API service, so we get that one.
			SespctApiService apiService = Context.getService(SespctApiService.class);
			
			if (apiService == null) {
				log.error("CRITICAL: SespctApiService could not be loaded. Please check the moduleApplicationContext.xml configuration.");
				return;
			}
			
			log.info("SespctApiService loaded successfully. Initiating a test API call to sync data...");
			
			// This line triggers the API communication test.
			apiService.syncPedidosFromApi();
			
			log.info("SESP-CT Module startup test sequence finished. Check the logs for API communication details.");
			
		}
		catch (Exception e) {
			log.error("A major error occurred during the SESP-CT Module startup test.", e);
		}
	}
	
	public void willStop() {
		log.info("Stopping SESP-CT Module");
	}
	
	public void stopped() {
		log.info("SESP-CT Module stopped");
	}
}
