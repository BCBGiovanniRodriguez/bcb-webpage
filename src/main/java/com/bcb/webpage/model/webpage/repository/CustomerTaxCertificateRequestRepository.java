package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificateRequests;

@Repository
public interface CustomerTaxCertificateRequestRepository extends JpaRepository<CustomerTaxCertificateRequests, Long> {

    

}
