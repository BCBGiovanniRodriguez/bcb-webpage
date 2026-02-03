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
@Table(name = "sisbur_risk_managements")
public class RiskManagementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long riskManagementId;

    private Integer type;

    private String filepath;

    private String filename;

    private String originalFilename;

    private Integer current;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private UserModel userModel;

    public static final Integer TYPE_RISK_MANAGEMENT = 1;

    public static final Integer TYPE_EVALUATION = 2;
    
    public static final String[] types = {"", "Administración de Riesgos", "Calificaciones"};
    
    public static final Integer SIMPLE_OPTION_YES = 1;

    public static final Integer SIMPLE_OPTION_NO = 2;

    public static final String[] options = {"", "Sí", "No"};
    
    public RiskManagementEntity() {
    }

    public Long getRiskManagementId() {
        return riskManagementId;
    }

    public void setRiskManagementId(Long riskManagementId) {
        this.riskManagementId = riskManagementId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getTypeAsString() {
        return RiskManagementEntity.types[this.getType()];
    }

    public String getCurrentAsString() {
        return RiskManagementEntity.options[this.getCurrent()];
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
