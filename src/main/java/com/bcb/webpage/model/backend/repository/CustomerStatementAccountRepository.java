package com.bcb.webpage.model.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.backend.entity.customers.CustomerContract;
import com.bcb.webpage.model.backend.entity.customers.CustomerStatementAccount;

@Repository
public interface CustomerStatementAccountRepository extends JpaRepository<CustomerStatementAccount, Long> {

    List<CustomerStatementAccount> findByYear(String year);

    List<CustomerStatementAccount> findByYearAndMonth(String year, String month);

    Optional<CustomerStatementAccount> findOneByCustomerContractAndYearAndMonth(CustomerContract customerContract, String year, String month);
    
}
