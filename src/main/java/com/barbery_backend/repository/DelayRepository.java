package com.barbery_backend.repository;

import com.barbery_backend.entity.Delay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DelayRepository extends JpaRepository<Delay, Long> {

    Optional<Delay> findByShopIdAndBarberId(
            Long shopId,
            Long barberId
    );

    Optional<Delay> findByCurrentQueueId(Long currentQueueId);

    Optional<Delay> findByCustomerIdAndBarberIdAndShopId(
            Long customerId,
            Long barberId,
            Long shopId
    );

}