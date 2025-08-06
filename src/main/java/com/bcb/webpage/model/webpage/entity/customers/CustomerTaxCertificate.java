package com.bcb.webpage.model.webpage.entity.customers;

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
@Table(name = "customer_tax_certificates")
public class CustomerTaxCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taxCertificateId;

    @ManyToOne
    @JoinColumn(name = "customerContractId", nullable = false)
    private CustomerContract customerContract;

    private String year;

    private String type;

    private String filename;

    private String path;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime downloadedDate;

    public static final String TAX_CERTIFICATE_INVOICE_STRING = "Factura";

    public static final String TAX_CERTIFICATE_INTEREST_STRING = "Intereses";
    
    public static final String TAX_CERTIFICATE_DIVIDENDS2013_STRING = "Dividendos2013";
    
    public static final String TAX_CERTIFICATE_DIVIDENDS2014_STRING = "Dividendos2014";
    
    public static final String TAX_CERTIFICATE_DIVIDENDSSIC_STRING = "DividendosSIC";
    
    public static final String TAX_CERTIFICATE_FORECLOSURE_STRING = "Enajenación";
    
    public static final String TAX_CERTIFICATE_FIBERS_STRING = "Fibras";

    public static final String TAX_CERTIFICATE_DERIVATIVES_STRING = "Derivados";

    public static final int TAX_CERTIFICATE_INVOICE = 0;

    public static final int TAX_CERTIFICATE_INTEREST = 1;

    public static final int TAX_CERTIFICATE_DIVIDENDS2013 = 2;

    public static final int TAX_CERTIFICATE_DIVIDENDS2014 = 3;

    public static final int TAX_CERTIFICATE_DIVIDENDSSIC = 4;

    public static final int TAX_CERTIFICATE_FORECLOSURE = 5;

    public static final int TAX_CERTIFICATE_FIBERS = 6;

    public static final int TAX_CERTIFICATE_DERIVATIVES = 7;


    public CustomerTaxCertificate() {
    }

    public Long getTaxCertificateId() {
        return taxCertificateId;
    }

    public void setTaxCertificateId(Long taxCertificateId) {
        this.taxCertificateId = taxCertificateId;
    }

    public CustomerContract getCustomerContract() {
        return customerContract;
    }

    public void setCustomerContract(CustomerContract customerContract) {
        this.customerContract = customerContract;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getDownloadedDate() {
        return downloadedDate;
    }

    public void setDownloadedDate(LocalDateTime downloadedDate) {
        this.downloadedDate = downloadedDate;
    }

    @Override
    public String toString() {
        return "CustomerTaxCertificate [taxCertificateId=" + taxCertificateId + ", customerContract=" + customerContract
                + ", year=" + year + ", type=" + type + ", filename=" + filename + ", path=" + path
                + ", downloadedDate=" + downloadedDate + "]";
    }

    public String getTypeAsString() {
        String result;

        switch (this.getType()) {
            case "0":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_INVOICE_STRING;
                break;
            case "1":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_INTEREST_STRING;
                break;
            case "2":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_DIVIDENDS2013_STRING;
                break;
            case "3":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_DIVIDENDS2014_STRING;
                break;
            case "4":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_DIVIDENDSSIC_STRING;
                break;
            case "5":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_FORECLOSURE_STRING;
                break;
            case "6":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_FIBERS_STRING;
                break;
            case "7":
                result = CustomerTaxCertificate.TAX_CERTIFICATE_DERIVATIVES_STRING;
                break;
            default:
                result = "OPCION NO CONOCIDA";
                break;
        }

        return result;
    }
    
}
