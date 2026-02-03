package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sisbur_investment_services")
public class InvestmentServicesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long investmentServicesId;

    private Integer type;

    private Integer current;

    private String filepath;

    private String filename;

    private String originalFilename;

    private Integer status;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private UserModel userModel;

    public static final Integer TYPE_INVESTMENT_SERVICE_GUIDE = 1;

    public static final Integer TYPE_CONTRACT = 2;

    public static final Integer TYPE_POLICIES = 3;

    public static final Integer TYPE_STOCK_MARKET_INFO = 4;

    public static final Integer TYPE_BEST_EXECUTION_INFO = 5;

    public static final String[] types = {"", "Guía de Servicios de Inversión", "Contrato de Intermediación Bursátil", "Marco General de Actuación", "Folleto de Mercado de Capitales", "Folleto Mejor Ejecución Funcionamiento del SOR"};

    public InvestmentServicesEntity() {
    }

    public Long getInvestmentServicesId() {
        return investmentServicesId;
    }

    public void setInvestmentServicesId(Long investmentServicesId) {
        this.investmentServicesId = investmentServicesId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
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

    public String getTypeAsString() {
        return InvestmentServicesEntity.types[this.getType()];
    }
}
