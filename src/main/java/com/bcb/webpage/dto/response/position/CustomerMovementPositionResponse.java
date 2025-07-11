package com.bcb.webpage.dto.response.position;

import java.util.ArrayList;

public class CustomerMovementPositionResponse {
    
    private String respuesta;
    
    private String descripcion;
    
    public ArrayList<Movimiento> movimiento;
    
    private ArrayList<Posicion> posicion;
    
    private ArrayList<Saldo> saldo;
    
    private String total;

    public CustomerMovementPositionResponse() {
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

    public ArrayList<Movimiento> getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(ArrayList<Movimiento> movimiento) {
        this.movimiento = movimiento;
    }

    public ArrayList<Posicion> getPosicion() {
        return posicion;
    }

    public void setPosicion(ArrayList<Posicion> posicion) {
        this.posicion = posicion;
    }

    public ArrayList<Saldo> getSaldo() {
        return saldo;
    }

    public void setSaldo(ArrayList<Saldo> saldo) {
        this.saldo = saldo;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CustomerMovementPositionResponse [respuesta=" + respuesta + ", descripcion=" + descripcion
                + ", movimiento=" + movimiento + ", posicion=" + posicion + ", saldo=" + saldo + ", total=" + total
                + "]";
    }
    
}
