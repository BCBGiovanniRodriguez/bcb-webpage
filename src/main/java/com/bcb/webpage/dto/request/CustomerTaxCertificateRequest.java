package com.bcb.webpage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerTaxCertificateRequest {

    @JsonProperty("contrato")
    public String contrato;

    @JsonProperty("anoconsulta")
    public String anoconsulta;

    @JsonProperty("pdf")
    public String pdf;

    @JsonProperty("constanciaTipo")
    public String constanciaTipo;

    public CustomerTaxCertificateRequest() {
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getAnoconsulta() {
        return anoconsulta;
    }

    public void setAnoconsulta(String anoconsulta) {
        this.anoconsulta = anoconsulta;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getConstanciaTipo() {
        return constanciaTipo;
    }

    public void setConstanciaTipo(String constanciaTipo) {
        this.constanciaTipo = constanciaTipo;
    }

    @Override
    public String toString() {
        return "CustomerTaxCertificateRequest [contrato=" + contrato + ", anoconsulta=" + anoconsulta + ", pdf=" + pdf
                + ", constanciaTipo=" + constanciaTipo + "]";
    }
    
}
