package com.bcb.webpage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerStatementAccountRequest {

    @JsonProperty("contrato")
    public String contrato;

    @JsonProperty("pdf")
    public String pdf;

    @JsonProperty("fecha")
    public String fecha;

    public CustomerStatementAccountRequest() {
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }
    
    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "CustomerStatementAccountRequest [contrato=" + contrato + "]";
    }
}
