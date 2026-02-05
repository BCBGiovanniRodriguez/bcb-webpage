package com.bcb.webpage.model.webpage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.PasswordResetToken;
import com.bcb.webpage.model.webpage.entity.customers.CustomerContract;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findOneByToken(String token);

    Optional<PasswordResetToken> findOneByTokenAndStatus(String token, Integer status);

    Optional<PasswordResetToken> findOneByCustomerContractAndStatus(CustomerContract contract, Integer status);

    List<PasswordResetToken> findAllByStatus(Integer status);
    
}
