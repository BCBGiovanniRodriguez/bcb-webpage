package com.bcb.webpage.dto.response.customer;

import java.util.ArrayList;

public class Cuenta {

    private ArrayList<ListaCuentum> listaCuenta;

    private String numCuenta;

    public Cuenta() { }

    public ArrayList<ListaCuentum> getListaCuenta() {
        return listaCuenta;
    }

    public void setListaCuenta(ArrayList<ListaCuentum> listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }
}
