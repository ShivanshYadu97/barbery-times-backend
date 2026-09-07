package com.barbery_backend.repository;

import com.barbery_backend.entity.CurrentQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrentQueueRepository extends JpaRepository<CurrentQueue, Long> {

    List<CurrentQueue> findByShopIdOrderByQueuePositionAsc(Long shopId);

    List<CurrentQueue> findByShopIdAndBarberIdOrderByQueuePositionAsc(Long shopId, Long barberId);

    Optional<CurrentQueue> findByShopIdAndCustomerId(Long shopId, Long customerId);

}


//Baad mein isi repository se hum:
//
//current queue nikalenge
//next position calculate karenge
//queue reorder karenge
//customer ko queue status denge