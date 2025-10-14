package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.bcb.webpage.model.webpage.entity.CommonEntity;
import com.bcb.webpage.model.webpage.entity.PasswordResetToken;

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
public class CustomerContract extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerContractId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerCustomer customer;

    private String contractNumber;

    private Integer current;

    private Integer status;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    @OneToMany(mappedBy = "customerContract")
    private Set<CustomerStatementAccount> statementAccounts;

    @OneToMany(mappedBy = "customerContract")
    private List<PasswordResetToken> passwordResetTokens;

    public static final Integer STATUS_ENABLED = 1;

    public static final Integer STATUS_DISABLED = 2;

    public static final String[] statuses = {"Seleccione Opción", "Habilitado", "Deshabilitado"};

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

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public boolean isCurrent() {
        return CustomerContract.CURRENT_TRUE == this.current;
    }

}
