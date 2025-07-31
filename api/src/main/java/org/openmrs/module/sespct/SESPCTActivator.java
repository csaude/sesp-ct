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
import org.openmrs.module.sespct.api.SESPCTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

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
		log.info("SESP-CT Module started");
		
		try {
			// Get the service that handles our business logic
			SESPCTService sespCtService = Context.getService(SESPCTService.class);
			
			// Create tables and populate with dummy data
			sespCtService.initializeModule();
			
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
}
