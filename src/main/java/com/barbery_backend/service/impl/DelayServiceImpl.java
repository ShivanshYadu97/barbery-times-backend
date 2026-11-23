package com.barbery_backend.service.impl;

import com.barbery_backend.entity.*;
import com.barbery_backend.repository.CurrentQueueRepository;
import com.barbery_backend.repository.DelayRepository;
import com.barbery_backend.service.DelayService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DelayServiceImpl implements DelayService {

    private final DelayRepository delayRepository;
    private final CurrentQueueRepository currentQueueRepository;
    private final TaskScheduler taskScheduler;

    @Override
    @Transactional
    public Delay saveOrUpdateDelay(
            Shop shop,
            Barber barber,
            User customer,
            CurrentQueue currentQueue,
            LocalDateTime estimatedStartTime) {

        Optional<Delay> existingDelay =
                delayRepository.findByShopIdAndBarberId(
                        shop.getId(),
                        barber.getId()
                );

        Delay delay;

        if (existingDelay.isPresent()) {

            delay = existingDelay.get();

            delay.setCustomer(customer);
            delay.setCurrentQueue(currentQueue);
            delay.setEstimatedStartTime(estimatedStartTime);

        } else {

            delay = new Delay();

            delay.setShop(shop);
            delay.setBarber(barber);
            delay.setCustomer(customer);
            delay.setCurrentQueue(currentQueue);
            delay.setEstimatedStartTime(estimatedStartTime);
        }

        return delayRepository.save(delay);
    }




    @Override
    @Transactional
    public String delayChecker(Long customerId, Long barberId, Long shopId) {

        // 1. Existing Delay row fetch karo
        Delay delay = delayRepository
                .findByCustomerIdAndBarberIdAndShopId(
                        customerId, barberId, shopId)
                .orElseThrow(() ->
                        new RuntimeException("Delay record not found"));

        // 2. EST check karo
        LocalDateTime currentTime = LocalDateTime.now();

        if (delay.getEstimatedStartTime().isAfter(currentTime)) {
            return null;
        }

        // 3. CurrentQueue record fetch karo
        CurrentQueue queue = currentQueueRepository
                .findByCustomerIdAndBarberIdAndShopId(
                        customerId, barberId, shopId)
                .orElseThrow(() ->
                        new RuntimeException("Queue record not found"));

        // 4. Service abhi start nahi hui
        if (queue.getServiceStartedAt() == null) {

            delay.setDelayStatus("ON");
            delay.setTriggerStartTime(currentTime);
            delayRepository.save(delay);

            return "ON";
        }

        // 5. Service start ho chuki hai - OFF sirf ON ke baad
        if ("ON".equals(delay.getDelayStatus())) {

            delay.setDelayStatus("OFF");
            delay.setTriggerEndTime(currentTime);
            delayRepository.save(delay);

            return "OFF";
        }

        return null;
    }




    public void delayStarter(
            Long customerId,
            Long barberId,
            Long shopId,
            LocalDateTime estimatedStartTime) {

        Runnable task = () -> {

            String status = delayChecker(
                    customerId,
                    barberId,
                    shopId
            );

            if (status != null) {
                System.out.println("Delay Status: " + status);

                // Yahan ON/OFF message bhejne ka logic aayega.
            }
        };

        Instant executionTime = estimatedStartTime
                .atZone(ZoneId.systemDefault())
                .toInstant();

        taskScheduler.schedule(task, executionTime);
    }

}