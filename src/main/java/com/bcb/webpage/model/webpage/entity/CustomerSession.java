package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.entity.customers.CustomerPositionReportRequests;
import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccount;
import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccountRequests;
import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_sessions")
public class CustomerSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "CustomerId", nullable = false)
    private CustomerCustomer customer;

    private String contractNumber;

    private boolean current;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime timestamp;

    private String remoteAddress;

    private String userAgent;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private List<CustomerMovementReport> reports;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private List<CustomerStatementAccount> statementAccounts;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private List<CustomerStatementAccountRequests> statementAccountRequests;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private List<CustomerTaxCertificate> taxtCertificates;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private List<CustomerPositionReportRequests> positionReportRequests;

    public CustomerSession() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public CustomerCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerCustomer customer) {
        this.customer = customer;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CustomerSession [SessionId=" + sessionId + ", customer=" + customer + ", current=" + current
                + ", timestamp=" + timestamp + "]";
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
        
}
