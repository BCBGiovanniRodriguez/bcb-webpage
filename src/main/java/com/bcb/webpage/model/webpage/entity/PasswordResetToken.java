package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resetTokenId;

    private String token;

    private LocalDateTime expirationDate;

    private Integer status;
    
    private LocalDateTime requestedDate;

    private String remoteIpAddressRequester;

    private String remoteUserAgentRequester;

    private LocalDateTime processedDate;

    private String remoteIpAddressProcessor;

    private String remoteUserAgentProcessor;

    @ManyToOne
    @JoinColumn(name = "CustomerContractId", nullable = false)
    private CustomerContract customerContract;

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

    public CustomerContract getCustomerContract() {
        return customerContract;
    }

    public void setCustomerContract(CustomerContract contract) {
        this.customerContract = contract;
    }
    
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public String getRemoteIpAddressRequester() {
        return remoteIpAddressRequester;
    }

    public void setRemoteIpAddressRequester(String remoteIpAddressRequester) {
        this.remoteIpAddressRequester = remoteIpAddressRequester;
    }

    public String getRemoteUserAgentRequester() {
        return remoteUserAgentRequester;
    }

    public void setRemoteUserAgentRequester(String remoteUserAgentRequester) {
        this.remoteUserAgentRequester = remoteUserAgentRequester;
    }

    public String getRemoteIpAddressProcessor() {
        return remoteIpAddressProcessor;
    }

    public void setRemoteIpAddressProcessor(String remoteIpAddressProcessor) {
        this.remoteIpAddressProcessor = remoteIpAddressProcessor;
    }

    public String getRemoteUserAgentProcessor() {
        return remoteUserAgentProcessor;
    }

    public void setRemoteUserAgentProcessor(String remoteUserAgentProcessor) {
        this.remoteUserAgentProcessor = remoteUserAgentProcessor;
    }
    
}
