package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;
import java.util.List;

import com.bcb.webpage.model.webpage.entity.CustomerSession;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_customers")
public class CustomerCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String customerKey;

    private String name;

    private String lastName;

    private String secondLastName;

    private String phoneNumber;

    private String email;

    private Integer initial;

    private Integer locked;

    private String password;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CustomerContract> contracts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CustomerSession> sessions;

    public CustomerCustomer() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getInitial() {
        return initial;
    }

    public void setInitial(Integer initial) {
        this.initial = initial;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<CustomerContract> getContracts() {
        return contracts;
    }

    public void setContracts(List<CustomerContract> contracts) {
        this.contracts = contracts;
    }

    public List<CustomerSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<CustomerSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        return "CustomerCustomer [customerId=" + customerId + ", customerKey=" + customerKey + ", name=" + name
                + ", lastName=" + lastName + ", secondLastName=" + secondLastName + ", phoneNumber=" + phoneNumber
                + ", email=" + email + ", initial=" + initial + ", locked=" + locked + ", password=" + password
                + ", created=" + created + "]";
    }

    public String getCustomerFullName() {
        String fullName = "";

        if (this.name != null) {
            fullName = name;
        }

        if (this.lastName != null) {
            fullName += " " + this.lastName;
        }

        if (this.secondLastName != null) {
            fullName += " " + this.secondLastName;
        }

        return fullName;
    }

}
