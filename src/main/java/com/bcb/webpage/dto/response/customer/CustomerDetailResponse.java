package com.bcb.webpage.dto.response.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerDetailResponse {

    private Cuenta cuenta;

    private Cotitulares cotitulares;
    
    private Beneficiarios beneficiarios;

    private Apoderados apoderados;

    private Autorizados autorizados;

    private String respuesta;

    private String descripcion;

    private String email;

    @JsonProperty("RFC") 
    private String rFC;
    
    @JsonProperty("CURP") 
    private String cURP;
    
    private String direccion;
    
    private String servicio;
    
    private String perfil;
    
    private String nombre;
    
    private String telOfi;
    
    private String telDom;
    
    private String telCel;

    public CustomerDetailResponse() {
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Cotitulares getCotitulares() {
        return cotitulares;
    }

    public void setCotitulares(Cotitulares cotitulares) {
        this.cotitulares = cotitulares;
    }

    public Beneficiarios getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(Beneficiarios beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public Apoderados getApoderados() {
        return apoderados;
    }

    public void setApoderados(Apoderados apoderados) {
        this.apoderados = apoderados;
    }

    public Autorizados getAutorizados() {
        return autorizados;
    }

    public void setAutorizados(Autorizados autorizados) {
        this.autorizados = autorizados;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getrFC() {
        return rFC;
    }

    public void setrFC(String rFC) {
        this.rFC = rFC;
    }

    public String getcURP() {
        return cURP;
    }

    public void setcURP(String cURP) {
        this.cURP = cURP;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelOfi() {
        return telOfi;
    }

    public void setTelOfi(String telOfi) {
        this.telOfi = telOfi;
    }

    public String getTelDom() {
        return telDom;
    }

    public void setTelDom(String telDom) {
        this.telDom = telDom;
    }

    public String getTelCel() {
        return telCel;
    }

    public void setTelCel(String telCel) {
        this.telCel = telCel;
    }

    @Override
    public String toString() {
        return "CustomerDetailResponse [cuenta=" + cuenta + ", cotitulares=" + cotitulares + ", beneficiarios="
                + beneficiarios + ", apoderados=" + apoderados + ", autorizados=" + autorizados + ", respuesta="
                + respuesta + ", descripcion=" + descripcion + ", email=" + email + ", rFC=" + rFC + ", cURP=" + cURP
                + ", direccion=" + direccion + ", servicio=" + servicio + ", perfil=" + perfil + ", nombre=" + nombre
                + ", telOfi=" + telOfi + ", telDom=" + telDom + ", telCel=" + telCel + "]";
    }    
}
