package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_statement_accounts")
public class CustomerStatementAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statementAccountId;

    @ManyToOne
    @JoinColumn(name = "customerContractId", nullable = false)
    private CustomerContract customerContract;
    
    private String year;
    
    private String month;

    private String path;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime downloadedDate;

    public static int STATEMENT_ACCOUNT_TYPE_PDF = 1;
    
    public static int STATEMENT_ACCOUNT_TYPE_XML = 2;

    public CustomerStatementAccount() {
    }

    public Long getStatementAccountId() {
        return statementAccountId;
    }

    public void setStatementAccountId(Long statementAccountId) {
        this.statementAccountId = statementAccountId;
    }

    public CustomerContract getCustomerContract() {
        return customerContract;
    }

    public void setCustomerContract(CustomerContract customerContract) {
        this.customerContract = customerContract;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getDownloadedDate() {
        return downloadedDate;
    }

    public void setDownloadedDate(LocalDateTime downloadedDate) {
        this.downloadedDate = downloadedDate;
    }

    @Override
    public String toString() {
        return "CustomerStatementAccount [statementAccountId=" + statementAccountId + ", customerContract="
                + customerContract + ", year=" + year + ", month=" + month + ", path=" + path + ", downloadedDate="
                + downloadedDate + "]";
    }
    
}
