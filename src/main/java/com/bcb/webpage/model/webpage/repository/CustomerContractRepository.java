package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerCustomer;

import java.util.List;
import java.util.Optional;


@Repository
public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long>{

    Optional<CustomerContract> findOneByContractNumber(String contractNumber);

    List<CustomerContract> findByCustomerAndCurrent(CustomerCustomer customer, Integer current);

    List<CustomerContract> findByStatus(Integer status);
    
    List<CustomerContract> findByCustomer(CustomerCustomer customer);

    @Query("SELECT cc.contractNumber FROM CustomerContract cc")
    List<String> findAllContractNumbers();
    
}
