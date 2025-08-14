package com.bcb.webpage.dto.response.position;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Saldo {
    
    @JsonProperty("CveDivisa")
    private String cveDivisa;
    
    @JsonProperty("SaldoActual")
    private String saldoActual;
    
    @JsonProperty("SaldoXLiquidar")
    private String saldoXLiquidar;
    
    @JsonProperty("SubTotal")
    private String subTotal;

    public Saldo() {
    }

    public String getCveDivisa() {
        return cveDivisa;
    }

    public void setCveDivisa(String cveDivisa) {
        this.cveDivisa = cveDivisa;
    }

    public String getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(String saldoActual) {
        this.saldoActual = saldoActual;
    }

    public String getSaldoXLiquidar() {
        return saldoXLiquidar;
    }

    public void setSaldoXLiquidar(String saldoXLiquidar) {
        this.saldoXLiquidar = saldoXLiquidar;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public String toString() {
        return "Saldo [cveDivisa=" + cveDivisa + ", saldoActual=" + saldoActual + ", saldoXLiquidar=" + saldoXLiquidar
                + ", subTotal=" + subTotal + "]";
    }
}
