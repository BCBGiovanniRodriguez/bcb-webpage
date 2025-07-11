package com.bcb.webpage.model.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.backend.entity.customers.CustomerContract;
import java.util.List;
import java.util.Optional;


@Repository
public interface ContractRepository extends JpaRepository<CustomerContract, Long>{

    Optional<CustomerContract> findOneByContractNumber(String contractNumber);
    
}
