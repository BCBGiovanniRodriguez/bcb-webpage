package com.bcb.webpage.model.webpage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.FinantialStatementEntity;

@Repository
public interface FinantialStatementRepository extends JpaRepository<FinantialStatementEntity, Long> {

    List<FinantialStatementEntity> findByType(Integer type);

    List<FinantialStatementEntity> findByTypeAndYear(Integer type, Integer year);

    List<FinantialStatementEntity> findByTypeAndYearAndPeriod(Integer type, Integer year, Integer period);

    Optional<FinantialStatementEntity> findOneByTypeAndYearAndStatus(Integer type, Integer year, Integer status);

    Optional<FinantialStatementEntity> findOneByTypeAndYearAndPeriodAndStatus(Integer type, Integer year, Integer period, Integer status);

    @Query("SELECT DISTINCT sfs.year FROM FinantialStatementEntity sfs ORDER BY sfs.year")
    List<String> findAllDistinctYears();
}
