package com.bcb.webpage.model.backend.entity.customers;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomerAuthenticated implements UserDetails {

    private CustomerContract contract;

    private CustomerCustomer customer;

    public CustomerAuthenticated() {
    }

    public CustomerAuthenticated(CustomerCustomer customer, CustomerContract contract) {
        this.customer = customer;
        this.contract = contract;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("Customer"));
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return this.contract.getContractNumber();
        //return customer.getCustomerKey();
    }

}
