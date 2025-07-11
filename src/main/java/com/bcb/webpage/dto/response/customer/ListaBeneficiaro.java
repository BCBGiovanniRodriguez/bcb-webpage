package com.bcb.webpage.dto.response.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListaBeneficiaro {

    @JsonProperty("Nombre") 
    private String nombre;

    public ListaBeneficiaro() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
