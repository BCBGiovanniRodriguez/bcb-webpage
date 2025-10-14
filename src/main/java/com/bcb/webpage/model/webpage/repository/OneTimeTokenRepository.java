package com.bcb.webpage.model.webpage.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.OneTimeTokenEntity;

import jakarta.transaction.Transactional;

@Repository
public interface OneTimeTokenRepository extends JpaRepository<OneTimeTokenEntity, String> {
    @Transactional
    List<OneTimeTokenEntity> deleteAllByExpiresAtBefore(Instant expiresAtBefore);

    List<OneTimeTokenEntity> findByExpiresAtBefore(Instant expiresAtBefore);
}
