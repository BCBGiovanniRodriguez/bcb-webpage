package com.bcb.webpage.model.backend.entity.customers;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_contracts")
public class CustomerContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerContractId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerCustomer customer;

    private String contractNumber;

    private Integer status;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    @OneToMany(mappedBy = "")
    private Set<CustomerStatementAccount> statementAccounts;

    public CustomerContract() {
    }

    public Long getCustomerContractId() {
        return customerContractId;
    }

    public void setCustomerContractId(Long contractId) {
        this.customerContractId = contractId;
    }

    public CustomerCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerCustomer customer) {
        this.customer = customer;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "CustomerContract [ContractId=" + customerContractId + ", customer=" + customer.toString() + ", contractNumber="
                + contractNumber + ", status=" + status + ", created=" + created + "]";
    }

}
