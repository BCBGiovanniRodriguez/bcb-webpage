package com.bcb.webpage.service.sisbur.model;

public class CustomerCashBalance {

    private Integer customerKey;

    private Integer contractNumber;

    private String currency;

    private Double balanceToday;

    private Double balance24;

    private Double balance48;

    private Double balance72;

    private Double balance96;

    private Double balance120;

    private Double holdBalance;

    public CustomerCashBalance() {
    }

    public Integer getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(Integer customerKey) {
        this.customerKey = customerKey;
    }

    public Integer getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(Integer contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getBalanceToday() {
        return balanceToday;
    }

    public void setBalanceToday(Double balanceToday) {
        this.balanceToday = balanceToday;
    }

    public Double getBalance24() {
        return balance24;
    }

    public void setBalance24(Double balance24) {
        this.balance24 = balance24;
    }

    public Double getBalance48() {
        return balance48;
    }

    public void setBalance48(Double balance48) {
        this.balance48 = balance48;
    }

    public Double getBalance72() {
        return balance72;
    }

    public void setBalance72(Double balance72) {
        this.balance72 = balance72;
    }

    public Double getBalance96() {
        return balance96;
    }

    public void setBalance96(Double balance96) {
        this.balance96 = balance96;
    }

    public Double getBalance120() {
        return balance120;
    }

    public void setBalance120(Double balance120) {
        this.balance120 = balance120;
    }

    public Double getHoldBalance() {
        return holdBalance;
    }

    public void setHoldBalance(Double holdBalance) {
        this.holdBalance = holdBalance;
    }
        
}
