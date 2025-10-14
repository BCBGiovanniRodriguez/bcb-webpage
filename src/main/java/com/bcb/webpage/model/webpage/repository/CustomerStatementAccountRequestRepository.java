package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.customers.CustomerStatementAccountRequests;

@Repository
public interface CustomerStatementAccountRequestRepository extends JpaRepository<CustomerStatementAccountRequests, Long> {

}
