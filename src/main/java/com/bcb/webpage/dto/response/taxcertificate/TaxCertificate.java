package com.bcb.webpage.dto.response.taxcertificate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaxCertificate {
    
    private String periodo;

    private String tipo;

    private String cotitular;

    public TaxCertificate() {
    }

    @JsonProperty("Periodo")
    public String getPeriodo() {
        return periodo;
    }

    @JsonProperty("Periodo")
    public void setPeriodo(String value) {
        this.periodo = value;
    }

    @JsonProperty("Tipo")
    public String getTipo() {
        return tipo;
    }

    @JsonProperty("Tipo")
    public void setTipo(String value) {
        this.tipo = value;
    }

    @JsonProperty("Cotitular")
    public String getCotitular() {
        return cotitular;
    }

    @JsonProperty("Cotitular")
    public void setCotitular(String value) {
        this.cotitular = value;
    }

    @Override
    public String toString() {
        return "TaxCertificate [periodo=" + periodo + ", tipo=" + tipo + ", cotitular=" + cotitular + "]";
    }
}
