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

    private Instant created;

    private Instant expiresAt;

    @Override
    @Nullable
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.getCreated() == null;
    }

    public OneTimeTokenEntity() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
    
}
