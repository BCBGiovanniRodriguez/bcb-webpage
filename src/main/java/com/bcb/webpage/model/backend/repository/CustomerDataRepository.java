package com.bcb.webpage.model.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.backend.entity.CustomerData;

@Repository
public interface CustomerDataRepository extends JpaRepository<CustomerData, Long> {

    CustomerData findTopByCustomerNumberAndRequestType(Long customerNumber, int requestType);

}
