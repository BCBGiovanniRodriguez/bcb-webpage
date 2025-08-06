package com.bcb.webpage.dto.response.taxcertificate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerTaxCertificateDetailResponse {

    private List<Period> listaAniosConstancias;

    private List<TaxCertificate> listaConstancias;
    
    private String respuesta;
    
    private String descripcion;

    @JsonProperty("listaAniosConstancias")
    public List<Period> getListaAniosConstancias() {
        return listaAniosConstancias;
    }

    @JsonProperty("listaAniosConstancias")
    public void setListaAniosConstancias(List<Period> value) {
        this.listaAniosConstancias = value;
    }

    @JsonProperty("listaConstancias")
    public List<TaxCertificate> getListaConstancias() {
        return listaConstancias;
    }

    @JsonProperty("listaConstancias")
    public void setListaConstancias(List<TaxCertificate> value) {
        this.listaConstancias = value;
    }

    @JsonProperty("respuesta")
    public String getRespuesta() {
        return respuesta;
    }

    @JsonProperty("respuesta")
    public void setRespuesta(String value) {
        this.respuesta = value;
    }

    @JsonProperty("descripcion")
    public String getDescripcion() {
        return descripcion;
    }

    @JsonProperty("descripcion")
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    @Override
    public String toString() {
        return "CustomerTaxCertificateDetailResponse [listaAniosConstancias=" + listaAniosConstancias
                + ", listaConstancias=" + listaConstancias + ", respuesta=" + respuesta + ", descripcion=" + descripcion
                + "]";
    }
    
}
