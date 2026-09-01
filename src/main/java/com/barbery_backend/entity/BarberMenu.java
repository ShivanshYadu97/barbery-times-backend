package com.barbery_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barber_menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarberMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

}
