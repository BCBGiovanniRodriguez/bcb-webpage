package com.bcb.webpage.dto.response.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaCuentum {

    @JsonProperty("Banco") 
    private String banco;

    @JsonProperty("Clabe") 
    private String clabe;

    public ListaCuentum() { }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }
}
