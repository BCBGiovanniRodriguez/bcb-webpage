package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resetTokenId;

    private String token;

    private LocalDateTime expirationDate;

    @OneToOne
    @JoinColumn(name = "customerContractId")
    private CustomerContract contract;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationDate);
    }

    public PasswordResetToken() {
    }

    public Long getResetTokenId() {
        return resetTokenId;
    }

    public void setResetTokenId(Long tokenId) {
        this.resetTokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CustomerContract getContract() {
        return contract;
    }

    public void setContract(CustomerContract contract) {
        this.contract = contract;
    }
}
