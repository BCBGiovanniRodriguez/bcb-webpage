package com.bcb.webpage.model.webpage.entity.customers;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.CustomerSession;

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

    private Integer type;

    private Integer owner;

    private Integer fileType;

    private String filename;

    private String path;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime downloadedDate;

    @ManyToOne
    @JoinColumn(name = "SessionId", nullable = false)
    private CustomerSession session;

    public static final String TAX_CERTIFICATE_OWNER_OWNER = "Titular";

    public static final String TAX_CERTIFICATE_OWNER_COOWNER = "Cotitular";

    public static final String[] ownership = {TAX_CERTIFICATE_OWNER_OWNER, TAX_CERTIFICATE_OWNER_COOWNER};

    public static final String TAX_CERTIFICATE_INVOICE_STRING = "Factura";

    public static final String TAX_CERTIFICATE_DIVIDENDS2014_STRING = "Dividendos 2014";

    public static final String TAX_CERTIFICATE_DIVIDENDS2013_STRING = "Dividendos 2013";

    public static final String TAX_CERTIFICATE_DIVIDENDSSIC_STRING = "Dividendos SIC";

    public static final String TAX_CERTIFICATE_DERIVATIVES_STRING = "Derivados";
    
    public static final String TAX_CERTIFICATE_FORECLOSURE_STRING = "Enajenación";

    public static final String TAX_CERTIFICATE_FIBERS_STRING = "Fibras";

    public static final String TAX_CERTIFICATE_INTEREST_STRING = "Intereses";

    public static final int TAX_CERTIFICATE_INVOICE = 0;

    public static final int TAX_CERTIFICATE_DIVIDENDS2014 = 1;
    
    public static final int TAX_CERTIFICATE_DIVIDENDS2013 = 2;
    
    public static final int TAX_CERTIFICATE_DIVIDENDSSIC = 3;
    
    public static final int TAX_CERTIFICATE_DERIVATIVES = 4;
    
    public static final int TAX_CERTIFICATE_FORECLOSURE = 5;
    
    public static final int TAX_CERTIFICATE_FIBERS = 6;
    
    public static final int TAX_CERTIFICATE_INTEREST = 7;

    public static final String[] taxCertificateTypes = {"",  TAX_CERTIFICATE_DIVIDENDS2014_STRING, TAX_CERTIFICATE_DIVIDENDS2013_STRING, TAX_CERTIFICATE_DIVIDENDSSIC_STRING, TAX_CERTIFICATE_DERIVATIVES_STRING, TAX_CERTIFICATE_FORECLOSURE_STRING, TAX_CERTIFICATE_FIBERS_STRING, TAX_CERTIFICATE_INTEREST_STRING};

    public static final int FILE_TYPE_PDF = 1;

    public static final int FILE_TYPE_XML = 2;

    public static final String[] fileTypes = {"PDF", "XML"};


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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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
    
    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getTypeAsString() throws Exception {

        if (this.getType() < 0 || this.getType() > CustomerTaxCertificate.taxCertificateTypes.length) {
            throw new Exception("Valor de tipo fuera de rango");
        }

        return CustomerTaxCertificate.taxCertificateTypes[this.getType()];
    }

    public String getFileTypeAsString() throws Exception {

        if (this.getFileType() < 0 || this.getFileType() > CustomerTaxCertificate.fileTypes.length) {
            throw new Exception("Valor de tipo de archivo fuera de rango");
        }

        return CustomerTaxCertificate.fileTypes[this.getFileType()];
    }
    
    public CustomerSession getSession() {
        return session;
    }

    public void setSession(CustomerSession session) {
        this.session = session;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }
    
}
