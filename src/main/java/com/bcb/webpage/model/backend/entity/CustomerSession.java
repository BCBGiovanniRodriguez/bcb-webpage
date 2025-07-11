package com.bcb.webpage.model.backend.entity;

import java.time.LocalDateTime;

import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private boolean current;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime timestamp;

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
}
