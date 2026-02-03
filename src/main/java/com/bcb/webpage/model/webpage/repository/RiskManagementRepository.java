package com.bcb.webpage.model.webpage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.RiskManagementEntity;

@Repository
public interface RiskManagementRepository extends JpaRepository<RiskManagementEntity, Long> {

    Optional<RiskManagementEntity> findOneByTypeAndCurrent(Integer type, Integer current);

}
