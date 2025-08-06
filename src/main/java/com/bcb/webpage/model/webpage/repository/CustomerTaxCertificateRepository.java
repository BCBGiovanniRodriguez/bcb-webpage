package com.bcb.webpage.model.webpage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;
import com.bcb.webpage.model.webpage.entity.customers.CustomerTaxCertificate;

@Repository
public interface CustomerTaxCertificateRepository extends JpaRepository<CustomerTaxCertificate, Long> {
    Optional<CustomerTaxCertificate> findByYearAndTypeAndCustomerContract(String year, String type, CustomerContract contract);
}
