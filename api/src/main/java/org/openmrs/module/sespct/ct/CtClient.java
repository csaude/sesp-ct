package org.openmrs.module.sespct.ct;

import com.fasterxml.jackson.databind.JsonNode;

public interface CtClient {
    JsonNode getPedidoById(String requestId, String facilityCode);
    JsonNode getPedidosSince(String sinceIso, String facilityCode);
}
