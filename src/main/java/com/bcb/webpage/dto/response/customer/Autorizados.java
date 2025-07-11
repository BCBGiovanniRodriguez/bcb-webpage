package com.bcb.webpage.dto.response.customer;

import java.util.ArrayList;

public class Autorizados {
    
    private String numAutorizado;

    private ArrayList<Object> listaAutorizado;

    public Autorizados() {
    }

    public String getNumAutorizado() {
        return numAutorizado;
    }

    public void setNumAutorizado(String numAutorizado) {
        this.numAutorizado = numAutorizado;
    }

    public ArrayList<Object> getListaAutorizado() {
        return listaAutorizado;
    }

    public void setListaAutorizado(ArrayList<Object> listaAutorizado) {
        this.listaAutorizado = listaAutorizado;
    }
}
