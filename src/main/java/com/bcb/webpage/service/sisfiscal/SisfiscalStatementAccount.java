package com.bcb.webpage.service.sisfiscal;

import java.time.LocalDate;

public class SisfiscalStatementAccount {

    private String contractNumber;

    private LocalDate date;

    private String pdfFile;

    private String xmlFile;

    private byte[] pdfFileByte;

    public SisfiscalStatementAccount() {
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public byte[] getPdfFileByte() {
        return pdfFileByte;
    }

    public void setPdfFileByte(byte[] pdfFileByte) {
        this.pdfFileByte = pdfFileByte;
    }

    

}
