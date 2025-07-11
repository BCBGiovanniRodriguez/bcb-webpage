package com.bcb.webpage.dto.request;

public class CustomerMovementPositionRequest {

    private String contrato;

    private String tipo;

    private String fechaIni;

    private String fechaFin;

    public CustomerMovementPositionRequest() {
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(String fechaIni) {
        this.fechaIni = fechaIni;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "CustomerMovementPositionRequest [contrato=" + contrato + ", tipo=" + tipo + ", fechaIni=" + fechaIni
                + ", fechaFin=" + fechaFin + "]";
    }
}
