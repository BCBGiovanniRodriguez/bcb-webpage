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
@Table(name = "sisbur_finantial_statements")
public class FinantialStatementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long finantialStatementId;

    private Integer type;

    private Integer year;

    private Integer period;

    private String path;

    private String fileName;

    private String originalfileName;

    private Integer status;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private UserModel userModel;

    public static final Integer TYPE_QUARTERLY = 1;
    
    public static final Integer TYPE_YEARLY = 2;

    public static final String[] types = {"", "Trimestral", "Anual"};

    public static final Integer PERIOD_ONE = 1;

    public static final Integer PERIOD_TWO = 2;

    public static final Integer PERIOD_THREE = 3;

    public static final Integer PERIOD_FOUR = 4;

    public static final String[] periods = {"", "T1", "T2", "T3", "T4"};

    public FinantialStatementEntity() {
    }

    public Long getFinantialStatementId() {
        return finantialStatementId;
    }

    public void setFinantialStatementId(Long finantialStatementId) {
        this.finantialStatementId = finantialStatementId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getOriginalfileName() {
        return originalfileName;
    }

    public void setOriginalfileName(String originalfileName) {
        this.originalfileName = originalfileName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTypeAsString() {
        return FinantialStatementEntity.types[this.type];
    }

    public String getPeriodAsString() {
        return FinantialStatementEntity.periods[this.period];
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
