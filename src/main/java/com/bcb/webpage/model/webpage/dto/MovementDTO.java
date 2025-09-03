package com.bcb.webpage.model.webpage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovementDTO {

    @JsonProperty("fechaOperacion")
    private String operationDate;

    @JsonProperty("fechaLiquidacion")
    private String saleDate;

    @JsonProperty("concepto")
    private String concept;

    @JsonProperty("emisora")
    private String emmiter;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("plazo")
    private String period;

    @JsonProperty("tasa")
    private String rate;

    @JsonProperty("precio")
    private String price;

    @JsonProperty("titulos")
    private String securities;

    @JsonProperty("importeBruto")
    private String savageAmount;

    @JsonProperty("comision")
    private String commision;

    @JsonProperty("iva")
    private String iva;

    @JsonProperty("isr")
    private String isr;

    @JsonProperty("importeNeto")
    private String amount;

    public MovementDTO() {
    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String date) {
        this.operationDate = date;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getEmmiter() {
        return emmiter;
    }

    public void setEmmiter(String emmiter) {
        this.emmiter = emmiter;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSecurities() {
        return securities;
    }

    public void setSecurities(String securities) {
        this.securities = securities;
    }

    public String getSavageAmount() {
        return savageAmount;
    }

    public void setSavageAmount(String savageAmount) {
        this.savageAmount = savageAmount;
    }

    public String getCommision() {
        return commision;
    }

    public void setCommision(String commision) {
        this.commision = commision;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getIsr() {
        return isr;
    }

    public void setIsr(String isr) {
        this.isr = isr;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    @Override
    public String toString() {
        return "MovementDTO [operationDate=" + operationDate + ", saleDate=" + saleDate + ", concept=" + concept
                + ", emmiter=" + emmiter + ", serie=" + serie + ", period=" + period + ", rate=" + rate + ", price="
                + price + ", securities=" + securities + ", savageAmount=" + savageAmount + ", commision=" + commision
                + ", iva=" + iva + ", isr=" + isr + ", amount=" + amount + "]";
    }

}
