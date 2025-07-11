package com.bcb.webpage.dto.request;

import java.util.Date;

public class MovementDto {
    
    private Integer customerNumber;

    private Integer type;

    private Date startDate;

    private Date endDate;

    public MovementDto() {
    }

    public MovementDto(Integer customerNumber, Integer type, Date startDate, Date endDate) {
        this.customerNumber = customerNumber;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    
}
