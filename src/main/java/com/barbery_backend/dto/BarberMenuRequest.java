package com.barbery_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberMenuRequest {

    private String name;

    private String description;

    private Integer durationMinutes;

    private Integer price;

    private Boolean active;

    private Long shopId;
}