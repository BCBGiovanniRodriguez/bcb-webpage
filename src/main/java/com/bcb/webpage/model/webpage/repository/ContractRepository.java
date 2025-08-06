package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContractRepository extends JpaRepository<CustomerContract, Long>{

    Optional<CustomerContract> findOneByContractNumber(String contractNumber);
    
}
