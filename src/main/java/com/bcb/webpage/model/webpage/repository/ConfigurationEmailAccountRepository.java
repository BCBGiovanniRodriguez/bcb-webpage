package com.bcb.webpage.model.webpage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.ConfigurationEmailAccountEntity;

@Repository
public interface ConfigurationEmailAccountRepository extends JpaRepository<ConfigurationEmailAccountEntity, Long>  {

    Optional<ConfigurationEmailAccountEntity> findOneByTypeAndMode(int type, int mode);

    Optional<ConfigurationEmailAccountEntity> findOneByTypeAndTargetAndMode(int type, int target, int mode);
    
}
