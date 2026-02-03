package com.bcb.webpage.model.webpage.entity.customers;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomerAuthenticated implements UserDetails {

    private CustomerContract contract;

    public CustomerAuthenticated() {
    }

    public CustomerAuthenticated(CustomerContract contract) {
        this.contract = contract;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("Customer"));
    }

    @Override
    public String getPassword() {
        return contract.getPassword();
    }

    @Override
    public String getUsername() {
        return this.contract.getContractNumber();
    }

    public CustomerContract getContract() {
        return contract;
    }
}
