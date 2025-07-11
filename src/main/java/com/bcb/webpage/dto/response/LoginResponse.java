package com.bcb.webpage.dto.response;

import jakarta.annotation.Nullable;

public class LoginResponse implements Response {

    private String respuesta;
    
    private String descripcion;
    
    private String requiereCambio;
    
    private String cliente;
    
    @Nullable
    private String nip;
    
    private String nombre;
    
    private String email;
    
    private String telefono;
    
    private String clienteBloqueado;
    
    private String numeroContrato;
    
    private String contratos;

    public LoginResponse() { }

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

    public String getRequiereCambio() {
        return requiereCambio;
    }

    public void setRequiereCambio(String requiereCambio) {
        this.requiereCambio = requiereCambio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getClienteBloqueado() {
        return clienteBloqueado;
    }

    public void setClienteBloqueado(String clienteBloqueado) {
        this.clienteBloqueado = clienteBloqueado;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getContratos() {
        return contratos;
    }

    public void setContratos(String contratos) {
        this.contratos = contratos;
    }

    @Override
    public String toString() {
        return "LoginResponse [respuesta=" + respuesta + ", descripcion=" + descripcion + ", requiereCambio="
                + requiereCambio + ", cliente=" + cliente + ", nip=" + nip + ", nombre=" + nombre + ", email=" + email
                + ", telefono=" + telefono + ", clienteBloqueado=" + clienteBloqueado + ", numeroContrato="
                + numeroContrato + ", contratos=" + contratos + "]";
    }

    
}
