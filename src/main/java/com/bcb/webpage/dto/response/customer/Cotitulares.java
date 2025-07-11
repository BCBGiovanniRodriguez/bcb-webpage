package com.bcb.webpage.dto.response.customer;

import java.util.ArrayList;

public class Cotitulares {
    
    private String numCotitular;
    
    private ArrayList<Object> listaCotitular;

    public Cotitulares() { }

    public String getNumCotitular() {
        return numCotitular;
    }

    public void setNumCotitular(String numCotitular) {
        this.numCotitular = numCotitular;
    }

    public ArrayList<Object> getListaCotitular() {
        return listaCotitular;
    }

    public void setListaCotitular(ArrayList<Object> listaCotitular) {
        this.listaCotitular = listaCotitular;
    }
}
