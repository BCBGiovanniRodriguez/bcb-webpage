package com.bcb.webpage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerDetailRequest implements AbstractRequestInterface {

    @JsonProperty("contrato")
    public String contrato;

    public CustomerDetailRequest() {
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    @Override
    public String toString() {
        return "CustomerDetailRequest [contrato=" + contrato + "]";
    }
}
