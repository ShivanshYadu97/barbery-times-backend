package com.barbery_backend.entity;


import com.barbery_backend.enums.EntryType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Processed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id")
    private Barber barber;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;

    @Column(nullable = false, length = 500)
    private String services;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Integer totalDurationMinutes;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime serviceStartedAt;

    private LocalDateTime serviceCompletedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessedStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}