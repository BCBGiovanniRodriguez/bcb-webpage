package com.bcb.webpage.dto.response;

public class NipChangeDto {
    private String answer;

    private String description;

    private String requiredChange;

    private String customer;

    private String nip;

    private String name;

    private String email;

    private String phone;

    private String customerBlocked;

    private String contractNumber;

    private String contracts;

    public NipChangeDto() {
    }

    public NipChangeDto(String answer, String description, String requiredChange, String customer, String nip,
            String name, String email, String phone, String customerBlocked, String contractNumber, String contracts) {
        this.answer = answer;
        this.description = description;
        this.requiredChange = requiredChange;
        this.customer = customer;
        this.nip = nip;
        this.name = name;
        this.email = email;
        this.phone = phone;
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
