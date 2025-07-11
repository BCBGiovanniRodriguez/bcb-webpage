package com.bcb.webpage.dto.response;

public class BeneficiaryDto {
    private String name;

    public BeneficiaryDto() {
    }

    public BeneficiaryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
