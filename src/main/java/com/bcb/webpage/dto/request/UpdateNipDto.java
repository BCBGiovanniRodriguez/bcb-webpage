package com.bcb.webpage.dto.request;

public class UpdateNipDto {
    private Long customerNumber;

    public String currentNip;

    public String updatedNip;

    public UpdateNipDto() {
    }

    public UpdateNipDto(Long customerNumber, String currentNip, String updatedNip) {
        this.customerNumber = customerNumber;
        this.currentNip = currentNip;
        this.updatedNip = updatedNip;
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCurrentNip() {
        return currentNip;
    }

    public void setCurrentNip(String currentNip) {
        this.currentNip = currentNip;
    }

    public String getUpdatedNip() {
        return updatedNip;
    }

    public void setUpdatedNip(String updatedNip) {
        this.updatedNip = updatedNip;
    }    
}
