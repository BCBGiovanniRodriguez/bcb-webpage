package com.bcb.webpage.model.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;
import java.util.Optional;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerCustomer, Long> {

    Optional<CustomerCustomer> findOneByCustomerKey(String customerKey);
    
}
