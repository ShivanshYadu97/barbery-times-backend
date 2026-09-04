package com.barbery_backend.dto;

import com.barbery_backend.enums.QueueStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueResponse {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long barberId;

    private Integer queuePosition;

    private String services;

    private Integer totalPrice;

    private Integer totalDurationMinutes;

    private LocalDateTime joinedAt;

    private LocalDateTime estimatedStartTime;

    private QueueStatus status;
}