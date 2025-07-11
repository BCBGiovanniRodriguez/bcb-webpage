package com.bcb.webpage.dto.request;

/**
 * 
 */
public class SisburRequest implements AbstractRequestInterface{

    private String contrato;

    private String anoconsulta;

    private String password;

    private String nip;

    private String nipNuevo;

    private String passwordNuevo;
    
    private String tipo;

    private String intento;

    private String fechaIni;

    private String fechaFin;

    private String pdf;

    private String constanciaTipo;

    private String mes;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNipNuevo() {
        return nipNuevo;
    }

    public void setNipNuevo(String nipNuevo) {
        this.nipNuevo = nipNuevo;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIntento() {
        return intento;
    }

    public void setIntento(String intento) {
        this.intento = intento;
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

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public SisburRequest() {
    }

    @Override
    public String toString() {
        return "SisburRequest [contrato=" + contrato + ", anoconsulta=" + anoconsulta + ", password=" + password
                + ", nip=" + nip + ", nipNuevo=" + nipNuevo + ", passwordNuevo=" + passwordNuevo + ", tipo=" + tipo
                + ", intento=" + intento + ", fechaIni=" + fechaIni + ", fechaFin=" + fechaFin + ", pdf=" + pdf
                + ", constanciaTipo=" + constanciaTipo + ", mes=" + mes + "]";
    }
}
