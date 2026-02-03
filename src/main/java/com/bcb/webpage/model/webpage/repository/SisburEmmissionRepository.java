package com.bcb.webpage.model.webpage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.SisburEmmission;

@Repository
public interface SisburEmmissionRepository extends JpaRepository<SisburEmmission, Long> {

    Optional<SisburEmmission> findOneByValueTypeAndEmmiterAndSerie(String valueType, String emmiter, String serie);

    @Query("SELECT se.emmissionId FROM SisburEmmission se")
    List<Long> findAllEmmissionIds();
    
}
