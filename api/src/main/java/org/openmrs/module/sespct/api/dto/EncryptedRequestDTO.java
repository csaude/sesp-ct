package org.openmrs.module.sespct.api.dto;

public class EncryptedRequestDTO {
    private String data;
    private String signature;

    // Constructors, Getters, and Setters
    public EncryptedRequestDTO(String data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}