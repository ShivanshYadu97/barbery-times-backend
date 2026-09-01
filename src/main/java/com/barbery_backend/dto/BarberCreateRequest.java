package com.barbery_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BarberCreateRequest {

    private String name;

    private String email;

    private String phone;
}