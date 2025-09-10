package org.openmrs.module.sespct.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarvUtils {

	private static final Logger log = LoggerFactory.getLogger(TarvUtils.class);
	
	private TarvUtils() {
        // Utility class, evitar instanciamento
    }
	
	public static String mapEsquemaTarvToUuid(String esquema) {
	    if (esquema == null) return null;

	    switch (esquema.trim().toUpperCase()) {
	        case "AZT+3TC+ABC":
	            return "3e7f46c7-a971-4c0c-82aa-a65589fd518e";
	        case "AZT+3TC+EFV":
	            return "e1de19fe-1d5f-11e0-b929-000c29ad1d07";
	        case "AZT+3TC+LPV":
	            return "28b28521-b6cd-454e-9ec5-f2c6c9c58468";
	        case "AZT+3TC+NVP":
	            return "e1dd2f44-1d5f-11e0-b929-000c29ad1d07";
	        case "TDF+3TC+EFV":
	            return "9dc17c1b-7b6d-488e-a38d-505a7b65ec82";
	        case "TDF+3TC+NVP":
	            return "2e44e77e-eac4-4f64-84d2-73d32abf94d5";
	        case "ABC+3TC+EFV":
	            return "78419317-cdda-42e9-92a3-13cb0cbf0020";
	        case "TDF+3TC+DTG":
	            return "e3f6bb60-e2cf-46cb-a9da-27d634ba8607";
	        case "ABC+3TC+DTG":
	            return "af15246d-30b8-4aff-8391-ca2b58e2c88b";
	        case "TDF+3TC+DTG2":
	            return "73add4be-d841-45ca-8554-e8001363f3bc";
	        case "ABC+3TC+LPV/R (2ª LINHA)":
	            return "e1da2d30-1d5f-11e0-b929-000c29ad1d07";
	        case "TDF+3TC+LPV/R (2ª LINHA)":
	            return "f8c5d365-7636-4449-9acd-c83c4fd2ea01";
	        case "ABC+TDF+LPV/R":
	            return "5044237e-ceba-4dc8-8368-bc54c5c82b84";
	        case "AZT+3TC+LPV/R (2ª LINHA)":
	            return "e1da3046-1d5f-11e0-b929-000c29ad1d07";
	        case "TDF+3TC+LPV/R+RTV":
	            return "d5f9d629-67bc-4f60-9e12-629e6d2c1d08";
	        case "AZT+3TC+ABC+LPV/R":
	            return "106e650c-0fe3-4193-acb4-74afe900382a";
	        case "TDF+AZT+3TC+LPV/R":
	            return "4d7a8dce-2115-4d18-a0ba-4add8aa2c7a1";
	        case "TDF+3TC+ATV/R":
	            return "7bf5a88d-6db6-4899-a01a-bfd14ce77b53";
	        case "ABC+3TC+ATV/R":
	            return "e8b741b3-463c-46b1-8423-a16f736af8d4";
	        case "AZT+3TC+ATV/R":
	            return "ba25f2b5-4216-4605-9e6b-1f591033dc3e";
	        case "ABC+3TC+ATV/R+RAL":
	            return "7d18d08c-d1a8-4dc8-8d65-02197b42acf7";
	        case "TDF+3TC+ATV/R+RAL":
	            return "c22c4431-a27e-4054-92bb-a1563482003e";
	        case "TDF+3TC+RAL+DRV/R":
	            return "46b8a2ab-36a7-4072-ab62-0af9b3b58e72";
	        case "ABC+3TC+DRV/R+RAL":
	            return "552dd71f-d1de-426a-a19c-49b8706a4a7f";
	        case "3TC+RAL+DRV/R":
	            return "f32e013c-38c8-483a-8012-88596e8b13da";
	        case "ABC+3TC+LPV":
	            return "cf05347e-063c-4896-91a4-097741cf6be6";
	        case "ABC+3TC+NVP":
	            return "e11be52e-0da1-4d32-ab5c-e0feb9b6abd6";
	        case "ABC+3TC+EFV (2ª LINHA)":
	            return "e1da2f42-1d5f-11e0-b929-000c29ad1d07";
	        case "TDF+3TC+DTG (2ª LINHA)":
	            return "e8341e56-eaf2-4530-8773-8fe05b400b5a";
	        case "ABC+3TC+DTG (2ª LINHA)":
	            return "6001b912-dd69-4331-ace1-899867f7015c";
	        case "AZT+3TC+RAL":
	            return "c4a56680-ac6e-4538-8126-e3097b7b4789";
	        case "AZT+3TC+DRV/R":
	            return "8f9c4e58-0d70-4b09-a109-e776d325e9fd";
	        case "AZT+3TC+RAL+DRV/R":
	            return "c30f826d-9433-4e63-ac91-dbbc158686ff";
	        case "AZT+3TC+DTG":
	            return "9cb63f72-4c08-4543-878a-537dcabe5670";
	        case "TDF+3TC+RAL":
	            return "0aa759a0-280a-48d4-b9aa-8e45d56b281f";
	        case "ABC+3TC+RAL":
	            return "7dbbd087-c9df-4465-9188-cc10b3c23e35";
	        case "ATV/R+TDF+3TC+DTG":
	            return "f42849f8-8c0a-4dcb-a589-792a9ea8b2ce";
	        default:
	            log.warn("Esquema TARV desconhecido: {}", esquema);
	            return null;
	    }
	}

}
