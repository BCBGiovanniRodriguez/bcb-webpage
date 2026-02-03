package com.bcb.webpage.model.webpage.dto;

import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralDTO implements PositionInterface {

    @JsonProperty("mercado")
    private String market;

    @JsonProperty("emisora")
    private String emmiter;

    @JsonProperty("serie")
    private String serie;

    @JsonProperty("titulos")
    private Double securities;

    @JsonProperty("plazo")
    private String period;

    @JsonProperty("tasa")
    private String rate;

    @JsonProperty("costoPromedio")
    private Double averageAmount;

    @JsonProperty("precio")
    private Double marketPrice;

    @JsonProperty("valor")
    private Double marketValue;

    @JsonProperty("porcentaje")
    private Double percentage;

    @JsonProperty("plusMinusValia")
    private Double capitalGainLoss;

    @JsonProperty("cssStyle")
    private String cssStyle;

    private Double dirtyPrice;

    private Double dirtyPrice24;

    private Double dirtyCost;

    private String holding;

    @JsonProperty("monto")
    private Double amount;

    private Double award;

    @JsonProperty("fechaInicial")
    private String averageDate;

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

    public Double getSecurities() {
        return securities;
    }

    public void setSecurities(Double securities) {
        this.securities = securities;
    }

    public Double getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(Double averageAmount) {
        this.averageAmount = averageAmount;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double price) {
        this.marketPrice = price;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double value) {
        this.marketValue = value;
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
                + securities + ", averageAmount=" + averageAmount + ", price=" + marketPrice + ", value=" + marketValue
                + ", percentage=" + percentage + ", capitalGainLoss=" + capitalGainLoss + "]";
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
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

    public Double getDirtyPrice() {
        return dirtyPrice;
    }

    public void setDirtyPrice(Double dirtyPrice) {
        this.dirtyPrice = dirtyPrice;
    }

    public Double getDirtyPrice24() {
        return dirtyPrice24;
    }

    public void setDirtyPrice24(Double dirtyPrice24) {
        this.dirtyPrice24 = dirtyPrice24;
    }

    public Double getDirtyCost() {
        return dirtyCost;
    }

    public void setDirtyCost(Double dirtyCost) {
        this.dirtyCost = dirtyCost;
    }

    public String getHolding() {
        return holding;
    }

    public void setHolding(String holding) {
        this.holding = holding;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAward() {
        return award;
    }

    public void setAward(Double award) {
        this.award = award;
    }

    public String getAverageDate() {
        return averageDate;
    }

    public void setAverageDate(String averageDate) {
        this.averageDate = averageDate;
    }
}
