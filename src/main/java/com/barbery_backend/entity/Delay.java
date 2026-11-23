package com.barbery_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "delay",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shop_barber",
                        columnNames = {"shop_id", "barber_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_queue_id", nullable = true)
    private CurrentQueue currentQueue;

    @Column(name = "estimated_start_time")
    private LocalDateTime estimatedStartTime;

    @Column(name = "delay_status")
    private String delayStatus;

    @Column(name = "trigger_start_time")
    private LocalDateTime triggerStartTime;

    @Column(name = "trigger_end_time")
    private LocalDateTime triggerEndTime;

}