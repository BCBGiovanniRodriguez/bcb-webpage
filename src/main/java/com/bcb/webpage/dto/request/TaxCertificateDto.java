package com.bcb.webpage.dto.request;

public class TaxCertificateDto {
    
    private Integer customerNumber;

    private Integer year;

    private Integer pdf;

    private Integer typeCertificate;

    public TaxCertificateDto() {
    }

    public TaxCertificateDto(Integer customerNumber, Integer year, Integer pdf, Integer typeCertificate) {
        this.customerNumber = customerNumber;
        this.year = year;
        this.pdf = pdf;
        this.typeCertificate = typeCertificate;
    }

    public Integer getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPdf() {
        return pdf;
    }

    public void setPdf(Integer pdf) {
        this.pdf = pdf;
    }

    public Integer getTypeCertificate() {
        return typeCertificate;
    }

    public void setTypeCertificate(Integer typeCertificate) {
        this.typeCertificate = typeCertificate;
    }
}
