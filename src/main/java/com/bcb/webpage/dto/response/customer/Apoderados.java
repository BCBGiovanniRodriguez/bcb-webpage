package com.bcb.webpage.dto.response.customer;

import java.util.ArrayList;

public class Apoderados {
    
    private String numApoderado;

    private ArrayList<Object> listaApoderados;

    public Apoderados() {
    }

    public String getNumApoderado() {
        return numApoderado;
    }

    public void setNumApoderado(String numApoderado) {
        this.numApoderado = numApoderado;
    }

    public ArrayList<Object> getListaApoderados() {
        return listaApoderados;
    }

    public void setListaApoderados(ArrayList<Object> listaApoderados) {
        this.listaApoderados = listaApoderados;
    }
}
