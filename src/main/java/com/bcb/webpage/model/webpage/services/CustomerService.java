package com.bcb.webpage.model.webpage.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;
import com.bcb.webpage.model.webpage.repository.CustomerContractRepository;
import com.bcb.webpage.model.webpage.repository.CustomerCustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerCustomerRepository customerRepository;

    @Autowired
    private CustomerContractRepository contractRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private CustomerCustomer customer;

    public void setCustomerRepository(CustomerCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setCustomer(CustomerCustomer customer) {
        this.customer = customer;
    }

    public List<CustomerContract> getCustomersContracts() {
        List<CustomerContract> contractList = null;

        try {
            contractList = contractRepository.findAll();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return contractList;
    }

    public List<CustomerCustomer> getCustomers() {
        List<CustomerCustomer> customerList = new ArrayList<>();

        try {
            customerList = customerRepository.findAll();
        } catch (Exception e) {
            System.out.println("Error on CustomerService::getCustomers " + e.getLocalizedMessage());
        }

        return customerList;
    }

    public void saveCustomers(List<Map<String, Object>> customerList) {

        try {
            String customerKey;
            CustomerCustomer customer;
            for (Map<String, Object> item : customerList) {
                LocalDateTime now = LocalDateTime.now();
                // Search for CVECLIENTE if not exist save, if exists get
                customerKey = item.get("CVECLIENTE").toString();
                Optional<CustomerCustomer> result = customerRepository.findOneByCustomerKey(customerKey);

                if (result.isPresent()) {
                    customer = result.get();
                } else {
                    customer = new CustomerCustomer();
                    customer.setCustomerKey(customerKey);
                    customer.setName(item.get("NOMCLIENTE").toString());
                    customer.setLastName(item.get("APEPATCLIENTE").toString());
                    customer.setSecondLastName(item.get("APEMATCLIENTE").toString());
                    customer.setPhoneNumber(item.get("TELDOM").toString());
                    customer.setEmail(item.get("EMAIL").toString());
                    customer.setInitial(Integer.parseInt(item.get("INICIAL").toString()));
                    customer.setLocked(Integer.parseInt(item.get("BLOQUEADO").toString()));
                    customer.setPassword(encoder.encode(item.get("PASSWORD").toString()));
                    customer.setCreated(now);
                    
                    customerRepository.saveAndFlush(customer);
                    System.out.println(customer.toString());
                }

                CustomerContract contract = new CustomerContract();
                contract.setCustomer(customer);
                contract.setContractNumber(item.get("CONTRATO").toString());
                contract.setStatus(1);
                contract.setCreated(now);
                System.out.println(contract.toString());

                contractRepository.saveAndFlush(contract);
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error on CustomerService::saveCustomers " + e.getLocalizedMessage());
        }

    }
    

}
