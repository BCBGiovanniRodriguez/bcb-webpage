package com.bcb.webpage.dto.response;

public class SessionDto {
    
    private String answer;

    private String description;

    private String requiredChange;

    private String customerNumber;

    private String nip;

    private String customerName;

    private String email;

    private String phoneNumber;

    private String customerBlocked;

    private String contractNumber;

    private String contracts;

    public SessionDto() {
    }

    public SessionDto(String answer, String description, String requiredChange, String customerNumber, String nip,
            String customerName, String email, String phoneNumber, String customerBlocked, String contractNumber,
            String contracts) {
        this.answer = answer;
        this.description = description;
        this.requiredChange = requiredChange;
        this.customerNumber = customerNumber;
        this.nip = nip;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.customerBlocked = customerBlocked;
        this.contractNumber = contractNumber;
        this.contracts = contracts;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredChange() {
        return requiredChange;
    }

    public void setRequiredChange(String requiredChange) {
        this.requiredChange = requiredChange;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerBlocked() {
        return customerBlocked;
    }

    public void setCustomerBlocked(String customerBlocked) {
        this.customerBlocked = customerBlocked;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContracts() {
        return contracts;
    }

    public void setContracts(String contracts) {
        this.contracts = contracts;
    }

    
    
}
