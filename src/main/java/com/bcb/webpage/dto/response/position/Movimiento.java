package com.bcb.webpage.dto.response.position;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Movimiento {

    @JsonProperty("FECOPERACION") 
    private String fecOperacion;

    @JsonProperty("DESCRIPTION") 
    private String descripcion;
    
    @JsonProperty("EMISORA") 
    private String emisora;
    
    @JsonProperty("SERIE") 
    private String serie;
    
    @JsonProperty("PLAZO") 
    private String plazo;
    
    @JsonProperty("TASA") 
    private String tasa;
    
    @JsonProperty("PRECIO") 
    private String precio;
    
    @JsonProperty("TITULOS") 
    private String titulos;
    
    @JsonProperty("IMPORTEBRUTO") 
    private String importeBruto;
    
    @JsonProperty("COMISION") 
    private String comision;
    
    @JsonProperty("IVA") 
    private String iva;
    
    @JsonProperty("ISR") 
    private String isr;
    
    @JsonProperty("IMPORTENETO") 
    private String importeNeto;
    
    @JsonProperty("FOLIO") 
    private String folio;

    public Movimiento() {
    }

    public String getFecOperacion() {
        return fecOperacion;
    }

    public void setFecOperacion(String fecOperacion) {
        this.fecOperacion = fecOperacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEmisora() {
        return emisora;
    }

    public void setEmisora(String emisora) {
        this.emisora = emisora;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getPlazo() {
        return plazo;
    }

    public void setPlazo(String plazo) {
        this.plazo = plazo;
    }

    public String getTasa() {
        return tasa;
    }

    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTitulos() {
        return titulos;
    }

    public void setTitulos(String titulos) {
        this.titulos = titulos;
    }

    public String getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(String importeBruto) {
        this.importeBruto = importeBruto;
    }

    public String getComision() {
        return comision;
    }

    public void setComision(String comision) {
        this.comision = comision;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getIsr() {
        return isr;
    }

    public void setIsr(String isr) {
        this.isr = isr;
    }

    public String getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(String importeNeto) {
        this.importeNeto = importeNeto;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    @Override
    public String toString() {
        return "Movimiento [fecOperacion=" + fecOperacion + ", descripcion=" + descripcion + ", emisora=" + emisora
                + ", serie=" + serie + ", plazo=" + plazo + ", tasa=" + tasa + ", precio=" + precio + ", titulos="
                + titulos + ", importeBruto=" + importeBruto + ", comision=" + comision + ", iva=" + iva + ", isr="
                + isr + ", importeNeto=" + importeNeto + ", folio=" + folio + "]";
    }
    
}
