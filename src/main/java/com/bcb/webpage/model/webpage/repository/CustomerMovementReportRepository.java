package com.bcb.webpage.model.webpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.CustomerMovementReport;

@Repository
public interface CustomerMovementReportRepository extends JpaRepository<CustomerMovementReport, Long> {

}
