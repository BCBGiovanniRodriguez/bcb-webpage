package com.bcb.webpage.model.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.backend.entity.CustomerSession;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {

}
