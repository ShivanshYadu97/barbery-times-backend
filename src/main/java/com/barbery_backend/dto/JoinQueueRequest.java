package com.barbery_backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinQueueRequest {

    private Long shopId;

    private Long customerId;

    private Long barberId;

    private List<Long> serviceIds;
}