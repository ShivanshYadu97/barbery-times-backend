package com.barbery_backend.repository;

import com.barbery_backend.entity.Processed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessedRepository extends JpaRepository<Processed, Long> {

    List<Processed> findByShopIdOrderByServiceCompletedAtDesc(Long shopId);
}