package com.barbery_backend.repository;

import com.barbery_backend.entity.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberRepository extends JpaRepository<Barber, Long> {

    List<Barber> findByShopId(Long shopId);

    List<Barber> findByShopIdAndIsActiveTrue(Long shopId);

    List<Barber> findByShopIdAndIsActiveTrueAndShiftActiveTrue(Long shopId);

}