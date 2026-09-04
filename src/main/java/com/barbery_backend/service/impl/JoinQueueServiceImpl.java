package com.barbery_backend.service.impl;

import com.barbery_backend.dto.JoinQueueRequest;
import com.barbery_backend.dto.QueueResponse;
import com.barbery_backend.entity.Barber;
import com.barbery_backend.entity.BarberMenu;
import com.barbery_backend.entity.CurrentQueue;
import com.barbery_backend.entity.Shop;
import com.barbery_backend.entity.User;
import com.barbery_backend.enums.QueueStatus;
import com.barbery_backend.repository.BarberMenuRepository;
import com.barbery_backend.repository.BarberRepository;
import com.barbery_backend.repository.CurrentQueueRepository;
import com.barbery_backend.repository.ShopRepository;
import com.barbery_backend.repository.UserRepository;
import com.barbery_backend.service.JoinQueueService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JoinQueueServiceImpl implements JoinQueueService {

    private final CurrentQueueRepository currentQueueRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final BarberMenuRepository barberMenuRepository;

    public JoinQueueServiceImpl(
            CurrentQueueRepository currentQueueRepository,
            ShopRepository shopRepository,
            UserRepository userRepository,
            BarberRepository barberRepository,
            BarberMenuRepository barberMenuRepository
    ) {
        this.currentQueueRepository = currentQueueRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.barberRepository = barberRepository;
        this.barberMenuRepository = barberMenuRepository;
    }

    //Join Queue
    @Override
    public CurrentQueue joinQueue(JoinQueueRequest request) {

        // 1. Validate Shop
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // 2. Shop must be open
        if (!shop.isOpen()) {
            throw new RuntimeException("Shop is currently closed");
        }

        // 3. Validate Customer
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 4. Validate selected Barber
        Barber barber = null;

        if (request.getBarberId() != null) {

            barber = barberRepository.findById(request.getBarberId())
                    .orElseThrow(() -> new RuntimeException("Barber not found"));

            if (!barber.getShop().getId().equals(shop.getId())) {
                throw new RuntimeException("Barber does not belong to this shop");
            }

            if (!barber.isActive()) {
                throw new RuntimeException("Barber is inactive");
            }

            if (!barber.isShiftActive()) {
                throw new RuntimeException("Barber is not currently on shift");
            }
        }

        // 5. Validate Services
        if (request.getServiceIds() == null || request.getServiceIds().isEmpty()) {
            throw new RuntimeException("At least one service must be selected");
        }

        List<BarberMenu> services =
                barberMenuRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new RuntimeException("One or more services not found");
        }

        // 6. Calculate total price and duration
        int totalPrice = 0;
        int totalDurationMinutes = 0;

        for (BarberMenu service : services) {

            if (!service.isActive()) {
                throw new RuntimeException(
                        "Service is inactive: " + service.getName()
                );
            }

            if (!service.getShop().getId().equals(shop.getId())) {
                throw new RuntimeException(
                        "Service does not belong to this shop: "
                                + service.getName()
                );
            }

            totalPrice += service.getPrice();
            totalDurationMinutes += service.getDurationMinutes();
        }

        // 7. Convert service names into comma-separated String
        String serviceNames = services.stream()
                .map(BarberMenu::getName)
                .collect(Collectors.joining(","));

        // 8. Get current queue
        List<CurrentQueue> currentQueue =
                currentQueueRepository
                        .findByShopIdOrderByQueuePositionAsc(shop.getId());

        // 9. Calculate next queue position
        int queuePosition = currentQueue.size() + 1;

        // 10. Current time
        LocalDateTime joinedAt = LocalDateTime.now();

        // 11. Calculate estimated start time
        LocalDateTime estimatedStartTime = joinedAt;

        for (CurrentQueue queueEntry : currentQueue) {

            if (queueEntry.getStatus() == QueueStatus.WAITING
                    || queueEntry.getStatus() == QueueStatus.CALLED
                    || queueEntry.getStatus() == QueueStatus.IN_SERVICE) {

                estimatedStartTime = estimatedStartTime.plusMinutes(
                        queueEntry.getTotalDurationMinutes()
                );
            }
        }

        // 12. Create CurrentQueue entity
        CurrentQueue queueEntry = CurrentQueue.builder()
                .shop(shop)
                .customer(customer)
                .barber(barber)
                .queuePosition(queuePosition)
                .services(serviceNames)
                .totalPrice(totalPrice)
                .totalDurationMinutes(totalDurationMinutes)
                .joinedAt(joinedAt)
                .estimatedStartTime(estimatedStartTime)
                .status(QueueStatus.WAITING)
                .build();

        // 13. Save queue entry
        return currentQueueRepository.save(queueEntry);
    }


    //Remove all from Queue
    @Override
    public void clearQueue() {
        currentQueueRepository.deleteAll();
    }

//    @Override
//    public List<QueueResponse> getQueueByShopAndBarber(Long shopId, Long barberId) {
//        return List.of();
//    }

    @Override
    public List<QueueResponse> getQueueByShopAndBarber(Long shopId, Long barberId) {

        List<CurrentQueue> queueEntries =
                currentQueueRepository
                        .findByShopIdAndBarberIdOrderByQueuePositionAsc(
                                shopId,
                                barberId
                        );

        return queueEntries.stream()
                .map(queue -> QueueResponse.builder()
                        .id(queue.getId())
                        .customerId(queue.getCustomer().getId())
                        .customerName(queue.getCustomer().getName())
                        .barberId(queue.getBarber() != null
                                ? queue.getBarber().getId()
                                : null)
                        .queuePosition(queue.getQueuePosition())
                        .services(queue.getServices())
                        .totalPrice(queue.getTotalPrice())
                        .totalDurationMinutes(queue.getTotalDurationMinutes())
                        .joinedAt(queue.getJoinedAt())
                        .estimatedStartTime(queue.getEstimatedStartTime())
                        .status(queue.getStatus())
                        .build()
                )
                .collect(Collectors.toList());
    }

}