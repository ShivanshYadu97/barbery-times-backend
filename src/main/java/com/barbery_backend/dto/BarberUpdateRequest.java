package com.barbery_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BarberUpdateRequest {

    private String name;

    private String email;

    private String phone;

    private boolean active;
}