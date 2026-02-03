package com.bcb.webpage.model.webpage.dto.customer;

public class CustomerBankAccount {

    private String institutionKey;

    private String institutionName;

    private String accountNumber;

    public CustomerBankAccount() {
    }

    public String getInstitutionKey() {
        return institutionKey;
    }

    public void setInstitutionKey(String institutionKey) {
        this.institutionKey = institutionKey;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
