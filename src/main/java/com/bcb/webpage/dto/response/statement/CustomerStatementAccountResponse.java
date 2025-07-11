package com.bcb.webpage.dto.response.statement;

import java.util.ArrayList;

public class CustomerStatementAccountResponse {
    
    private ArrayList<StatementAccount> listaEstadosCuenta;

    private String respuesta;
    
    private String descripcion;

    public CustomerStatementAccountResponse() {
    }

    public ArrayList<StatementAccount> getListaEstadosCuenta() {
        return listaEstadosCuenta;
    }

    public void setListaEstadosCuenta(ArrayList<StatementAccount> listaEstadosCuenta) {
        this.listaEstadosCuenta = listaEstadosCuenta;
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
        return "StatementAccountResponse [listaEstadosCuenta=" + listaEstadosCuenta + ", respuesta=" + respuesta
                + ", descripcion=" + descripcion + "]";
    }
}
