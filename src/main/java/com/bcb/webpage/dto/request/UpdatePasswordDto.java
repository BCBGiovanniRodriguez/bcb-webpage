package com.bcb.webpage.dto.request;

public class UpdatePasswordDto {
    
    private String customerNumber;

    private String currentPassword;

    private String newPassword;

    public UpdatePasswordDto() { }

    public UpdatePasswordDto(String customerNumber, String currentPassword, String newPassword) {
        this.customerNumber = customerNumber;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
