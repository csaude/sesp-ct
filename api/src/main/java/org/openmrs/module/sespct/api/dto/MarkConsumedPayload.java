package org.openmrs.module.sespct.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class MarkConsumedPayload {
    private String status = "CONSUMED";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> pedidoUuids;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> respostaUuids;

    // Getters and Setters

    public List<String> getPedidoUuids() {
        return pedidoUuids;
    }

    public void setPedidoUuids(List<String> pedidoUuids) {
        this.pedidoUuids = pedidoUuids;
    }

    public List<String> getRespostaUuids() {
        return respostaUuids;
    }

    public void setRespostaUuids(List<String> respostaUuids) {
        this.respostaUuids = respostaUuids;
    }
}
