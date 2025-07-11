package com.bcb.webpage.dto.response.statement;

public class CustomerStatementFileResponse {

    private String respuesta;
    
    private String descripcion;

    private String constancia;

    public CustomerStatementFileResponse() {
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

    public String getConstancia() {
        return constancia;
    }

    public void setConstancia(String constancia) {
        this.constancia = constancia;
    }

    @Override
    public String toString() {
        return "CustomerStatementFileResponse [respuesta=" + respuesta + ", descripcion=" + descripcion + "]";
    }
    
}
