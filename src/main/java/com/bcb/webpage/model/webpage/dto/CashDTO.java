package com.bcb.webpage.model.webpage.dto;

import com.bcb.webpage.model.webpage.dto.interfaces.PositionInterface;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CashDTO implements PositionInterface{

    @JsonProperty("moneda")
    private String currencyName;

    @JsonProperty("saldoPorLiquidar")
    private Double pendingBalance;

    @JsonProperty("saldoActual")
    private Double currentBalance;

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

    public void setPendingBalance(Double subtotal) {
        this.pendingBalance = subtotal;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double total) {
        this.currentBalance = total;
    }

}