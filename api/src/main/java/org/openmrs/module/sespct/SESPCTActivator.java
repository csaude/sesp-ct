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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class SESPCTActivator extends BaseModuleActivator {
	
	private static final Logger log = LoggerFactory.getLogger(SESPCTActivator.class);
	
	@Override
	public void willStart() {
		log.debug("Starting SESPCT Module");
		Context.getAdministrationService().setGlobalProperty("sespct.extensions.loaded", "true");
	}
	
	@Override
	public void started() {
		log.info("Started SESPCT Module");
	}
	
	@Override
	public void willStop() {
		log.info("Stopping SESPCT - trying to clean up extensions");
		try {
			org.openmrs.module.Module module = org.openmrs.module.ModuleFactory.getModuleById("sespct");
			if (module != null) {
				module.getExtensions().clear();
				log.info("Extensions cleared successfully.");
			} else {
				log.warn("Could not find module 'sespct' to clear extensions.");
			}
		}
		catch (Exception e) {
			log.error("Error while trying to clear extensions on stop", e);
		}
	}
	
	@Override
	public void stopped() {
		log.info("Stopped SESPCT Module");
	}
}
