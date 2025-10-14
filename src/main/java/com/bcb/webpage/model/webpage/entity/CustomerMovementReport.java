package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_movement_reports")
public class CustomerMovementReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movementReportId;

    private String contractNumber;

    private LocalDate starDate;

    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String reportData;

    private LocalDateTime requestedDate;

    @ManyToOne
    @JoinColumn(name = "SessionId", nullable = false)
    private CustomerSession session;

    public CustomerMovementReport() {
    }

    public Long getMovementReportId() {
        return movementReportId;
    }

    public void setMovementReportId(Long movementReportId) {
        this.movementReportId = movementReportId;
    }

    public LocalDate getStarDate() {
        return starDate;
    }

    public void setStarDate(LocalDate starDate) {
        this.starDate = starDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public CustomerSession getSession() {
        return session;
    }

    public void setSession(CustomerSession session) {
        this.session = session;
    }

}
