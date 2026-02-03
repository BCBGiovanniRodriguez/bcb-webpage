package com.bcb.webpage.model.webpage.dto.customer;

public class CustomerAttorney {

    private String fullName;

    private String fullAddress;

    private String contractNumber;

    private Integer operationAuthorized;

    public static final Integer YES = 1;

    public static final Integer NO = 0;

    public CustomerAttorney() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Integer getOperationAuthorized() {
        return operationAuthorized;
    }

    public void setOperationAuthorized(Integer operationAuthorized) {
        this.operationAuthorized = operationAuthorized;
    }

    

}
