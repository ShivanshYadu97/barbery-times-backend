package com.barbery_backend.service;

import com.barbery_backend.entity.*;

import java.time.LocalDateTime;

public interface DelayService {

    Delay saveOrUpdateDelay(
            Shop shop,
            Barber barber,
            User customer,
            CurrentQueue currentQueue,
            LocalDateTime estimatedStartTime
    );

    String delayChecker(
            Long customerId,
            Long barberId,
            Long shopId
    );

    void delayStarter(
            Long customerId,
            Long barberId,
            Long shopId,
            LocalDateTime estimatedStartTime
    );

}
