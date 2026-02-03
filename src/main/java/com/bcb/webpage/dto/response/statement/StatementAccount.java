package com.bcb.webpage.dto.response.statement;

public class StatementAccount {
    
    private String contrato;
    
    private String anio;
    
    private String mes;
    
    private String fecha;

    private String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    public StatementAccount() {
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "StatementAccount [contrato=" + contrato + ", anio=" + anio + ", mes=" + mes + ", fecha=" + fecha + "]";
    }

    public String getMesAsString() {
        return meses[Integer.parseInt(this.mes) - 1];
    }

}
