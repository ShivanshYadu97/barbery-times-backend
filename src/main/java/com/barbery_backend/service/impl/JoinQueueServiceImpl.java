package com.barbery_backend.service.impl;

import com.barbery_backend.dto.JoinQueueRequest;
import com.barbery_backend.dto.QueueResponse;
import com.barbery_backend.entity.*;
import com.barbery_backend.enums.QueueStatus;
import com.barbery_backend.repository.*;
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
    private final ProcessedRepository processedRepository;

    public JoinQueueServiceImpl(
            CurrentQueueRepository currentQueueRepository,
            ShopRepository shopRepository,
            UserRepository userRepository,
            BarberRepository barberRepository,
            BarberMenuRepository barberMenuRepository,
            ProcessedRepository processedRepository
    ) {
        this.currentQueueRepository = currentQueueRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.barberRepository = barberRepository;
        this.barberMenuRepository = barberMenuRepository;
        this.processedRepository = processedRepository;
    }

    // ==========================================
    // JOIN QUEUE
    // ==========================================

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
        if (request.getServiceIds() == null ||
                request.getServiceIds().isEmpty()) {

            throw new RuntimeException(
                    "At least one service must be selected"
            );
        }

        List<BarberMenu> services =
                barberMenuRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new RuntimeException(
                    "One or more services not found"
            );
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
                        .findByShopIdOrderByQueuePositionAsc(
                                shop.getId()
                        );

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

                estimatedStartTime =
                        estimatedStartTime.plusMinutes(
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

                // Service abhi start nahi hui
                .serviceStartedAt(null)

                .status(QueueStatus.WAITING)
                .build();

        // 13. Save queue entry
        return currentQueueRepository.save(queueEntry);
    }


    // ==========================================
    // REMOVE ALL FROM QUEUE
    // ==========================================

    @Override
    public void clearQueue() {
        currentQueueRepository.deleteAll();
    }


    // ==========================================
    // GET QUEUE BY SHOP + BARBER
    // ==========================================

    @Override
    public List<QueueResponse> getQueueByShopAndBarber(
            Long shopId,
            Long barberId
    ) {

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
                        .barberId(
                                queue.getBarber() != null
                                        ? queue.getBarber().getId()
                                        : null
                        )
                        .queuePosition(queue.getQueuePosition())
                        .services(queue.getServices())
                        .totalPrice(queue.getTotalPrice())
                        .totalDurationMinutes(
                                queue.getTotalDurationMinutes()
                        )
                        .joinedAt(queue.getJoinedAt())
                        .estimatedStartTime(
                                queue.getEstimatedStartTime()
                        )

                        // IMPORTANT:
                        // Refresh ke baad timer ke liye
                        // service start time frontend ko bhejna hai
                        .serviceStartedAt(
                                queue.getServiceStartedAt()
                        )

                        .status(queue.getStatus())
                        .build()
                )
                .collect(Collectors.toList());
    }


    // ==========================================
    // GET QUEUE BY SHOP + CUSTOMER
    // ==========================================

    @Override
    public QueueResponse getQueueByShopAndCustomer(
            Long shopId,
            Long customerId
    ) {

        CurrentQueue queue =
                currentQueueRepository
                        .findByShopIdAndCustomerId(
                                shopId,
                                customerId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer is not currently in the queue"
                                )
                        );

        return QueueResponse.builder()
                .id(queue.getId())
                .customerId(queue.getCustomer().getId())
                .customerName(queue.getCustomer().getName())
                .barberId(
                        queue.getBarber() != null
                                ? queue.getBarber().getId()
                                : null
                )
                .queuePosition(queue.getQueuePosition())
                .services(queue.getServices())
                .totalPrice(queue.getTotalPrice())
                .totalDurationMinutes(
                        queue.getTotalDurationMinutes()
                )
                .joinedAt(queue.getJoinedAt())
                .estimatedStartTime(
                        queue.getEstimatedStartTime()
                )

                // IMPORTANT:
                // Customer side ko bhi service start time milega
                .serviceStartedAt(
                        queue.getServiceStartedAt()
                )

                .status(queue.getStatus())
                .build();
    }


    // ==========================================
    // START SERVICE AT BARBER SIDE
    // ==========================================

    @Override
    public CurrentQueue startService(Long queueId) {

        // 1. Queue entry find karo
        CurrentQueue queue =
                currentQueueRepository
                        .findById(queueId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Queue entry not found"
                                )
                        );

        // 2. Sirf WAITING customer hi service start kar sakta hai
        if (queue.getStatus() != QueueStatus.WAITING) {

            throw new RuntimeException(
                    "Customer is not in WAITING status"
            );
        }

        // 3. Service start time save karo
        queue.setServiceStartedAt(
                LocalDateTime.now()
        );

        // 4. Status WAITING → IN_SERVICE
        queue.setStatus(
                QueueStatus.IN_SERVICE
        );

        // 5. Updated queue database mein save karo
        return currentQueueRepository.save(queue);
    }



    // ==========================================
    // STOP SERVICE AT BARBER SIDE
    // ==========================================


    @Override
    public void stopService(Long queueId) {

        CurrentQueue queue = currentQueueRepository
                .findById(queueId)
                .orElseThrow(() ->
                        new RuntimeException("Queue entry not found")
                );

        if (queue.getStatus() != QueueStatus.IN_SERVICE) {
            throw new RuntimeException(
                    "Customer is not currently in service"
            );
        }

        LocalDateTime serviceCompletedAt =
                LocalDateTime.now();

        // 1. Move completed customer to Processed
        Processed processed = Processed.builder()
                .transactionId(null)
                .shop(queue.getShop())
                .customer(queue.getCustomer())
                .barber(queue.getBarber())
                .services(queue.getServices())
                .totalPrice(queue.getTotalPrice())
                .totalDurationMinutes(queue.getTotalDurationMinutes())
                .joinedAt(queue.getJoinedAt())
                .serviceStartedAt(queue.getServiceStartedAt())
                .serviceCompletedAt(serviceCompletedAt)
                .status(ProcessedStatus.COMPLETED)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(null)
                .createdAt(serviceCompletedAt)
                .build();

        processedRepository.save(processed);

        // 2. Remove current customer
        currentQueueRepository.delete(queue);

        // 3. Get remaining customers of same barber
        List<CurrentQueue> remainingQueue =
                currentQueueRepository
                        .findByShopIdAndBarberIdOrderByQueuePositionAsc(
                                queue.getShop().getId(),
                                queue.getBarber().getId()
                        );

        // 4. Recalculate position + estimated start time
        int position = 1;

        LocalDateTime nextStartTime =
                serviceCompletedAt;

        for (CurrentQueue remaining : remainingQueue) {

            // Queue position
            remaining.setQueuePosition(position++);

            // First waiting customer starts NOW
            remaining.setEstimatedStartTime(
                    nextStartTime
            );

            // Next customer's estimated time
            nextStartTime =
                    nextStartTime.plusMinutes(
                            remaining.getTotalDurationMinutes()
                    );
        }

        // 5. Save updated queue
        currentQueueRepository.saveAll(remainingQueue);
    }


    // ==========================================
    // MARK NOT PRESENT AT BARBER SIDE
    // ==========================================

    @Override
    public void markNotPresent(Long queueId) {

        // 1. Queue entry find karo
        CurrentQueue queue = currentQueueRepository
                .findById(queueId)
                .orElseThrow(() ->
                        new RuntimeException("Queue entry not found")
                );

        // 2. Sirf WAITING customer ko
        //    Not Present mark kar sakte hain
        if (queue.getStatus() != QueueStatus.WAITING) {
            throw new RuntimeException(
                    "Only WAITING customer can be marked as not present"
            );
        }

        // 3. Current time
        LocalDateTime processedAt = LocalDateTime.now();

        // 4. Processed record create karo
        Processed processed = Processed.builder()
                .transactionId(null)
                .shop(queue.getShop())
                .customer(queue.getCustomer())
                .barber(queue.getBarber())
                .services(queue.getServices())
                .totalPrice(queue.getTotalPrice())
                .totalDurationMinutes(queue.getTotalDurationMinutes())
                .joinedAt(queue.getJoinedAt())
                .serviceStartedAt(null)
                .serviceCompletedAt(null)
                .status(ProcessedStatus.SKIPPED)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(null)
                .createdAt(processedAt)
                .build();

        // 5. Processed table mein save
        processedRepository.save(processed);

        // 6. Current queue se customer remove
        currentQueueRepository.delete(queue);

        // 7. Remaining queue nikalo
        List<CurrentQueue> remainingQueue =
                currentQueueRepository
                        .findByShopIdAndBarberIdOrderByQueuePositionAsc(
                                queue.getShop().getId(),
                                queue.getBarber().getId()
                        );

        // 8. Queue positions dobara calculate karo
        int position = 1;

        for (CurrentQueue remaining : remainingQueue) {
            remaining.setQueuePosition(position++);
        }

        // 9. Updated positions save karo
        currentQueueRepository.saveAll(remainingQueue);
    }


    // ==========================================
    // ADD EXTRA SERVICE AT BARBER SIDE
    // ==========================================


    @Override
    public CurrentQueue addExtraService(Long queueId, Long serviceId) {

        // 1. Current customer find
        CurrentQueue queue = currentQueueRepository
                .findById(queueId)
                .orElseThrow(() ->
                        new RuntimeException("Queue entry not found")
                );

        // 2. Sirf current customer / IN_SERVICE customer ke liye
        if (queue.getStatus() != QueueStatus.IN_SERVICE) {
            throw new RuntimeException(
                    "Extra service can only be added to current customer"
            );
        }

        // 3. Extra service find
        BarberMenu extraService = barberMenuRepository
                .findById(serviceId)
                .orElseThrow(() ->
                        new RuntimeException("Service not found")
                );

        // 4. Service active honi chahiye
        if (!extraService.isActive()) {
            throw new RuntimeException(
                    "Service is inactive: " + extraService.getName()
            );
        }

        // 5. Service same shop ki honi chahiye
        if (!extraService.getShop().getId()
                .equals(queue.getShop().getId())) {

            throw new RuntimeException(
                    "Service does not belong to this shop"
            );
        }

        // 6. Existing services mein extra service add
        queue.setServices(
                queue.getServices()
                        + ","
                        + extraService.getName()
        );

        // 7. Price increase
        queue.setTotalPrice(
                queue.getTotalPrice()
                        + extraService.getPrice()
        );

        // 8. Duration increase
        queue.setTotalDurationMinutes(
                queue.getTotalDurationMinutes()
                        + extraService.getDurationMinutes()
        );

        // 9. Current queue save
        currentQueueRepository.save(queue);


        // ==========================================
        // RECALCULATE REMAINING QUEUE ETA
        // ==========================================

        List<CurrentQueue> barberQueue =
                currentQueueRepository
                        .findByShopIdAndBarberIdOrderByQueuePositionAsc(
                                queue.getShop().getId(),
                                queue.getBarber().getId()
                        );

        LocalDateTime nextStartTime = null;

        for (CurrentQueue customer : barberQueue) {

            // Current customer jo already IN_SERVICE hai
            if (customer.getStatus() == QueueStatus.IN_SERVICE
                    && customer.getServiceStartedAt() != null) {

                LocalDateTime serviceEndTime =
                        customer.getServiceStartedAt()
                                .plusMinutes(
                                        customer.getTotalDurationMinutes()
                                );

                nextStartTime = serviceEndTime;

            } else {

                // WAITING customer ka estimated start
                if (nextStartTime != null) {

                    customer.setEstimatedStartTime(
                            nextStartTime
                    );

                    nextStartTime =
                            nextStartTime.plusMinutes(
                                    customer.getTotalDurationMinutes()
                            );
                }
            }
        }

        // 10. Updated ETA sab customers ke DB mein save
        currentQueueRepository.saveAll(barberQueue);


        // 11. Updated current customer return
        return queue;
    }

}