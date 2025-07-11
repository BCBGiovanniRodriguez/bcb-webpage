package com.bcb.webpage.model.backend.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sisbur_customer_data")
public class CustomerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long customerDataId;

    @Column(nullable = false)
    private Long customerNumber;

    @Column(nullable = false)
    private int requestType;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String data;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String md5String;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private Date created;

    public static int REQUEST_LOGIN = 1;

    public static int REQUEST_PASSWORD_UPDATE = 2;

    public static int REQUEST_NIP_UPDATE = 3;

    public static int REQUEST_CUSTOMER_DETAIL = 4;

    public static int REQUEST_CUSTOMER_MOVEMENTS = 5;

    public static int REQUEST_CUSTOMER_POSITION_BALANCE = 6;

    public static int REQUEST_CUSTOMER_RESULTS = 7;

    public static int REQUEST_CUSTOMER_STATEMENTS_FILE = 8;

    public static int REQUEST_CUSTOMER_STATEMENTS_DATA = 10;

    public CustomerData() {
    }

    public Long getCustomerDataId() {
        return customerDataId;
    }

    public void setCustomerDataId(Long customerDataId) {
        this.customerDataId = customerDataId;
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
