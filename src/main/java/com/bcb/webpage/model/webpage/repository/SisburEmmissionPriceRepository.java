package com.bcb.webpage.model.webpage.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bcb.webpage.model.webpage.entity.SisburEmmissionPrice;
import com.bcb.webpage.model.webpage.entity.SisburEmmission;

@Repository
public interface SisburEmmissionPriceRepository extends JpaRepository<SisburEmmissionPrice, Long> {

    //List<SisburEmmisionPrice> findTopNByOrderByCreatedDesc(int n);
    //List<SisburEmmissionPrice> findTop20ByEmmisionIdByOrderByCreatedDesc(SisburEmmission emmissionId);

    Optional<SisburEmmissionPrice> findOneByEmmissionIdAndCreatedLessThanEqual(SisburEmmission sisburEmmission, LocalDateTime created);

    Optional<SisburEmmissionPrice> findFirstByEmmissionIdAndCreatedLessThanEqualOrderByCreatedDesc(SisburEmmission sisburEmmission, LocalDateTime created);

    List<SisburEmmissionPrice> findByEmmissionId(SisburEmmission sisburEmmission);

    //List<SisburEmmisionPrice> findTop20ByOrderByCreatedDesc();

}
