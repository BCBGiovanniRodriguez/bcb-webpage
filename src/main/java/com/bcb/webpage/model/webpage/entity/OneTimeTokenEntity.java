package com.bcb.webpage.model.webpage.entity;

import java.time.Instant;

import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_ott")
public class OneTimeTokenEntity implements Persistable<String> {

    @Id
    @Column(name = "ottId")
    private String id;

    private String contractNumber;

    private String email;

    private Instant expiresAt;
    
    private Instant requestedDate;
    
    private String remoteIpAddressRequester;
    
    private String remoteUserAgentRequester;

    private Instant processedDate;

    private String remoteIpAddressProcessor;

    private String remoteUserAgentProcessor;

    private Integer state;

    public static final Integer STATE_CREATED = 1;

    public static final Integer STATE_CONSUMED = 2;

    public static final Integer STATE_EXPIRED = 3;

    public static final String[] states = {"Desconocido", "creado", "procesado", "caducado"};

    @Override
    @Nullable
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return this.getRequestedDate() == null;
    }

    public OneTimeTokenEntity() {
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Instant requestedDate) {
        this.requestedDate = requestedDate;
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

    public Instant getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Instant processedDate) {
        this.processedDate = processedDate;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    
    public String getStateAsString() {
        return OneTimeTokenEntity.states[this.state];
    }
}
