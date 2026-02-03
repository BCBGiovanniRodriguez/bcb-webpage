package com.bcb.webpage.model.webpage.dto.customer;

public class CustomerBeneficiary {

    private Long beneficiaryId;

    private String fullName;

    private String fullAddress;

    private Double percentage;

    private Long contractNumber;

    private Integer gender;

    public CustomerBeneficiary() {
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
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

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Long getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(Long contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
}
