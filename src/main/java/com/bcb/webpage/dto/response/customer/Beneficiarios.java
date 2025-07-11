package com.bcb.webpage.dto.response.customer;

import java.util.ArrayList;

public class Beneficiarios {
    
    private String numBeneficiario;
    
    private ArrayList<ListaBeneficiaro> listaBeneficiaros;

    public Beneficiarios() {
    }

    public String getNumBeneficiario() {
        return numBeneficiario;
    }

    public void setNumBeneficiario(String numBeneficiario) {
        this.numBeneficiario = numBeneficiario;
    }

    public ArrayList<ListaBeneficiaro> getListaBeneficiaros() {
        return listaBeneficiaros;
    }

    public void setListaBeneficiaros(ArrayList<ListaBeneficiaro> listaBeneficiaros) {
        this.listaBeneficiaros = listaBeneficiaros;
    }
}
