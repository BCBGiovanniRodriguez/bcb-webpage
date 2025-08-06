package com.bcb.webpage.dto.report;

public class MoneyMarketDto {

    private String emmiter;

    private String series;

    private String period;

    private Double rate;

    private String position;

    private Double amount;

    private Double currentValue;

    private Double marketValue;

    private Double valuation;

    public MoneyMarketDto() {
    }

    public String getEmmiter() {
        return emmiter;
    }

    public void setEmmiter(String emmiter) {
        this.emmiter = emmiter;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public Double getValuation() {
        return valuation;
    }

    public void setValuation(Double valuation) {
        this.valuation = valuation;
    }

    @Override
    public String toString() {
        return "MoneyMarketDto [emmiter=" + emmiter + ", series=" + series + ", period=" + period + ", rate=" + rate
                + ", position=" + position + ", amount=" + amount + ", currentValue=" + currentValue + ", marketValue="
                + marketValue + ", valuation=" + valuation + "]";
    }
}
