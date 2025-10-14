package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.CustomerSession;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_position_report_requests")
public class CustomerPositionReportRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionReportRequestId;

    private String contractNumber;

    private Integer type;

    @Lob
    private String data;

    private LocalDateTime requestedDate;

    @ManyToOne
    @JoinColumn(name = "SessionId", nullable = false)
    private CustomerSession session;

    public static final Integer TYPE_GENERAL = 1;

    public static final Integer TYPE_CASH = 2;

    public static final Integer TYPE_STOCK_MARKET = 3;

    public static final Integer TYPE_MONEY_MARKET = 4;

    public static final String[] types = {"", "General", "Efectivo", "Capitales", "Dinero", "FondosInversion"};

    public CustomerPositionReportRequests() {
    }

    public Long getPositionReportRequestId() {
        return positionReportRequestId;
    }

    public void setPositionReportRequestId(Long positionReportRequestId) {
        this.positionReportRequestId = positionReportRequestId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public CustomerSession getSession() {
        return session;
    }

    public void setSession(CustomerSession session) {
        this.session = session;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

}
