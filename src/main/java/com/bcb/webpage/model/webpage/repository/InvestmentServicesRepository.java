package com.bcb.webpage.model.webpage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.InvestmentServicesEntity;

@Repository
public interface InvestmentServicesRepository extends JpaRepository<InvestmentServicesEntity, Long> {

    List<InvestmentServicesEntity> findByType(Integer type);

    List<InvestmentServicesEntity> findByTypeAndCurrent(Integer type, Integer current);

    Optional<InvestmentServicesEntity> findOneByTypeAndCurrent(Integer type, Integer current);
}
