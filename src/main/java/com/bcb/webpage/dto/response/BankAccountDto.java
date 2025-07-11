package com.bcb.webpage.dto.response;

public class BankAccountDto {
    private String bank;

    private String clabe;

    public BankAccountDto() {
    }

    public BankAccountDto(String bank, String clabe) {
        this.bank = bank;
        this.clabe = clabe;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }
}
