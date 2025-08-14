package com.bcb.webpage.model.webpage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralDTO {

    @JsonProperty("mercado")
    private String market;

    @JsonProperty("emisora")
    private String emmiter;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("titulos")
    private String securities;

    @JsonProperty("costoPromedio")
    private Double averageAmount;

    @JsonProperty("precio")
    private Double price;

    @JsonProperty("valor")
    private Double value;

    @JsonProperty("porcentaje")
    private Double percentage;

    @JsonProperty("plusMinusValia")
    private Double capitalGainLoss;

    @JsonProperty("cssStyle")
    private String cssStyle;

    public GeneralDTO() {
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
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

    public String getSecurities() {
        return securities;
    }

    public void setSecurities(String securities) {
        this.securities = securities;
    }

    public Double getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(Double averageAmount) {
        this.averageAmount = averageAmount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getCapitalGainLoss() {
        return capitalGainLoss;
    }

    public void setCapitalGainLoss(Double capitalGainLoss) {
        this.capitalGainLoss = capitalGainLoss;
    }

    @Override
    public String toString() {
        return "GeneralDTO [market=" + market + ", emmiter=" + emmiter + ", serie=" + serie + ", securities="
                + securities + ", averageAmount=" + averageAmount + ", price=" + price + ", value=" + value
                + ", percentage=" + percentage + ", capitalGainLoss=" + capitalGainLoss + "]";
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    
    
}
