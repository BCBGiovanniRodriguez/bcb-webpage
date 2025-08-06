package com.bcb.webpage.dto.response.taxcertificate;

import java.util.Arrays;

public class CustomerTaxCertificatePeriodResponse {

    private Period[] listaAniosConstancias;
    
    private String respuesta;

    private String descripcion;

    public CustomerTaxCertificatePeriodResponse() {
    }

    public Period[] getListaAniosConstancias() {
        return listaAniosConstancias;
    }

    public void setListaAniosConstancias(Period[] listaAniosConstancias) {
        this.listaAniosConstancias = listaAniosConstancias;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "CustomerTaxCertificatePeriodResponse [listaAniosConstancias=" + Arrays.toString(listaAniosConstancias)
                + ", respuesta=" + respuesta + ", descripcion=" + descripcion + "]";
    }

    
}
