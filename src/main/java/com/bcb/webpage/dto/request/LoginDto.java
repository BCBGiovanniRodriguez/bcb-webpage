package com.bcb.webpage.dto.request;

public class LoginDto {

    public String customerNumber;

    public String password;
    
    public Integer attempt;

    public LoginDto() {
    }

    public LoginDto(String customerNumber, String password, Integer attempt) {
        this.customerNumber = customerNumber;
        this.password = password;
        this.attempt = attempt;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }
    
}
