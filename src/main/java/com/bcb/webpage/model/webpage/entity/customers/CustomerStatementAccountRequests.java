package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.CustomerSession;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_statement_account_requests")
public class CustomerStatementAccountRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statementAccountRequestId;

    private String contractNumber;
    
    @ManyToOne
    @JoinColumn(name = "StatementAccountId", nullable = false)
    private CustomerStatementAccount statementAccount;
    
    @ManyToOne
    @JoinColumn(name = "SessionId", nullable = false)
    private CustomerSession session;
    
    private LocalDateTime requestedDate;

    public CustomerStatementAccountRequests() {
    }

    public Long getStatementAccountRequestId() {
        return statementAccountRequestId;
    }

    public void setStatementAccountRequestId(Long statementAccountRequestId) {
        this.statementAccountRequestId = statementAccountRequestId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public CustomerStatementAccount getStatementAccount() {
        return statementAccount;
    }

    public void setStatementAccount(CustomerStatementAccount statementAccount) {
        this.statementAccount = statementAccount;
    }

    public CustomerSession getSession() {
        return session;
    }

    public void setSession(CustomerSession session) {
        this.session = session;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

}
