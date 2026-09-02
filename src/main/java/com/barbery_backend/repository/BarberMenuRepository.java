package com.barbery_backend.repository;

import com.barbery_backend.entity.BarberMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberMenuRepository extends JpaRepository<BarberMenu, Long> {

    List<BarberMenu> findByShopId(Long shopId);

    List<BarberMenu> findByShopIdAndIsActiveTrue(Long shopId);
}