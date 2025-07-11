package com.bcb.webpage.model.backend.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.backend.repository.CustomerRepository;

public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerCustomer customer;

    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setCustomer(CustomerCustomer customer) {
        this.customer = customer;
    }

    //public void getCus

}
