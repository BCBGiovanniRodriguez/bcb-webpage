package com.bcb.webpage.dto.request;

public class PositionDto {
    
    private Integer contractNumber;

    private Integer type;

    public PositionDto() {
    }

    public PositionDto(Integer contractNumber, Integer type) {
        this.contractNumber = contractNumber;
        this.type = type;
    }

    public Integer getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(Integer contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
