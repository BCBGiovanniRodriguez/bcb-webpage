package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.CustomerSession;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_tax_certificates_requests")
public class CustomerTaxCertificateRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taxCertificateRequestId;

    private String contractNumber;
    
    @ManyToOne
    @JoinColumn(name = "TaxCertificateId", nullable = false)
    private CustomerTaxCertificate taxCertificate;
    
    @ManyToOne
    @JoinColumn(name = "SessionId", nullable = false)
    private CustomerSession session;
    
    private LocalDateTime requestedDate;

    public CustomerTaxCertificateRequests() {
    }

    public Long getTaxCertificateRequestId() {
        return taxCertificateRequestId;
    }

    public void setTaxCertificateRequestId(Long taxCertificateRequestId) {
        this.taxCertificateRequestId = taxCertificateRequestId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public CustomerTaxCertificate getTaxCertificate() {
        return taxCertificate;
    }

    public void setTaxCertificate(CustomerTaxCertificate taxCertificate) {
        this.taxCertificate = taxCertificate;
    }

    public CustomerSession getSession() {
        return session;
    }

    public void setSession(CustomerSession session) {
        this.session = session;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    
}
