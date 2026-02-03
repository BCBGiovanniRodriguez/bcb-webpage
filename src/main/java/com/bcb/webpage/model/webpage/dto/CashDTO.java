package com.bcb.webpage.model.webpage.dto;

import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CashDTO implements PositionInterface {

    @JsonProperty("moneda")
    private String currencyName;

    @JsonProperty("saldoPorLiquidar")
    private Double pendingBalance;

    @JsonProperty("saldoActual")
    private Double currentBalance;

    @JsonProperty("SubTotal")
    private Double subTotal;

    public CashDTO() {
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(Double pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public String toString() {
        return "CashDTO [currencyName=" + currencyName + ", pendingBalance=" + pendingBalance + ", currentBalance="
                + currentBalance + ", subTotal=" + subTotal + "]";
    }

}