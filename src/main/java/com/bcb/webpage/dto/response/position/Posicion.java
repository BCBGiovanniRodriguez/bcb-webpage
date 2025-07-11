package com.bcb.webpage.dto.response.position;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Posicion {

    private final int MARKET_TYPE_STOCK = 1;

    private final int MARKET_TYPE_MONEY = 2;

    private final int MARKET_TYPE_FUNDS = 3;
    
    @JsonProperty("EMISORA") 
    private String emisora;
    
    @JsonProperty("SERIE") 
    private String serie;
    
    @JsonProperty("PLAZO") 
    private String plazo;
    
    @JsonProperty("TASA") 
    private String tasa;
    
    @JsonProperty("TITULOS") 
    private String titulos;
    
    @JsonProperty("COSTOXTITULOS") 
    private String costoXTitulos;
    
    @JsonProperty("COSTOPROMEDIO") 
    private String costoPromedio;
    
    @JsonProperty("VALORMERCADO") 
    private String valorMercado;
    
    @JsonProperty("PLUS/MINU") 
    private String plusMinusvalia;
    
    @JsonProperty("MERCADO") 
    private String mercado;
    
    @JsonProperty("SUBTOTALDIN") 
    private String subtotalDin;
    
    @JsonProperty("SUBTOTALCAP") 
    private String subtotalCap;
    
    @JsonProperty("SUBTOTALFON") 
    private String subtotalFon;

    public Posicion() {
        emisora = "";
        serie = "";
        plazo = "";
        tasa = "";
        titulos = "";
        costoXTitulos = "";
        costoPromedio = "";
        valorMercado = "";
        plusMinusvalia = "";
        mercado = "";
        subtotalDin = "";
        subtotalCap = "";
        subtotalFon = "";
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

    public String getTitulos() {
        return titulos;
    }

    public void setTitulos(String titulos) {
        this.titulos = titulos;
    }

    public String getCostoXTitulos() {
        return costoXTitulos;
    }

    public void setCostoXTitulos(String costoXTitulos) {
        this.costoXTitulos = costoXTitulos;
    }

    public String getCostoPromedio() {
        return costoPromedio;
    }

    public void setCostoPromedio(String costoPromedio) {
        this.costoPromedio = costoPromedio;
    }

    public String getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(String valorMercado) {
        this.valorMercado = valorMercado;
    }

    public String getPlusMinusvalia() {
        return plusMinusvalia;
    }

    public void setPlusMinusvalia(String plusMinusvalia) {
        this.plusMinusvalia = plusMinusvalia;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
    }

    public String getSubtotalDin() {
        return subtotalDin;
    }

    public void setSubtotalDin(String subtotalDin) {
        this.subtotalDin = subtotalDin;
    }

    public String getSubtotalCap() {
        return subtotalCap;
    }

    public void setSubtotalCap(String subtotalCap) {
        this.subtotalCap = subtotalCap;
    }

    public String getSubtotalFon() {
        return subtotalFon;
    }

    public void setSubtotalFon(String subtotalFon) {
        this.subtotalFon = subtotalFon;
    }

    public boolean isStockMarket() {
        
        return (this.mercado != null) && (Integer.parseInt(this.mercado) == MARKET_TYPE_STOCK);
    }

    public boolean isMoneyMarket() {
        return (this.mercado != null) && (Integer.parseInt(this.mercado) == MARKET_TYPE_MONEY);
    }

    public boolean isFundsMarket() {
        return (this.mercado != null) && (Integer.parseInt(this.mercado) == MARKET_TYPE_FUNDS);
    }
}
