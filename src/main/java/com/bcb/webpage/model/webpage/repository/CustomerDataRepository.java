package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.CustomerData;

@Repository
public interface CustomerDataRepository extends JpaRepository<CustomerData, Long> {

    CustomerData findTopByCustomerNumberAndRequestType(Long customerNumber, int requestType);

    CustomerData findTopByCustomerNumberAndRequestTypeAndHash(Long customerNumber, int requestType, String hash);

}
