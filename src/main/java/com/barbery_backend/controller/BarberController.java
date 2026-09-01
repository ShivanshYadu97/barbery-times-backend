package com.barbery_backend.controller;

import com.barbery_backend.dto.BarberCreateRequest;
import com.barbery_backend.dto.BarberUpdateRequest;
import com.barbery_backend.entity.Barber;
import com.barbery_backend.service.BarberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }


    // Create barber for a shop
    @PostMapping("/shop/{shopId}")
    public ResponseEntity<Barber> createBarber(
            @PathVariable Long shopId,
            @RequestBody BarberCreateRequest request
    ) {

        Barber barber = new Barber();

        barber.setName(request.getName());
        barber.setEmail(request.getEmail());
        barber.setPhone(request.getPhone());

        return ResponseEntity.ok(
                barberService.createBarber(shopId, barber)
        );
    }


    // Get all barbers of a shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Barber>> getBarbersByShop(
            @PathVariable Long shopId
    ) {

        return ResponseEntity.ok(
                barberService.getBarbersByShop(shopId)
        );
    }


    // Get single barber
    @GetMapping("/{barberId}")
    public ResponseEntity<Barber> getBarberById(
            @PathVariable Long barberId
    ) {

        return ResponseEntity.ok(
                barberService.getBarberById(barberId)
        );
    }


    // Update barber details
    @PutMapping("/{barberId}")
    public ResponseEntity<Barber> updateBarber(
            @PathVariable Long barberId,
            @RequestBody BarberUpdateRequest request
    ) {

        Barber barber = new Barber();

        barber.setName(request.getName());
        barber.setEmail(request.getEmail());
        barber.setPhone(request.getPhone());
        barber.setActive(request.isActive());

        return ResponseEntity.ok(
                barberService.updateBarber(barberId, barber)
        );
    }


    // Remove / deactivate barber
    @DeleteMapping("/{barberId}")
    public ResponseEntity<Void> deleteBarber(
            @PathVariable Long barberId
    ) {

        barberService.deleteBarber(barberId);

        return ResponseEntity.noContent().build();
    }


    // Update barber shift status
    @PatchMapping("/{barberId}/shift")
    public ResponseEntity<Barber> updateShiftStatus(
            @PathVariable Long barberId,
            @RequestParam boolean shiftActive
    ) {

        return ResponseEntity.ok(
                barberService.updateShiftStatus(
                        barberId,
                        shiftActive
                )
        );
    }


    // Get barbers for customer
    @GetMapping("/shop/{shopId}/customer")
    public ResponseEntity<List<Barber>> getBarbersForCustomer(
            @PathVariable Long shopId
    ) {

        return ResponseEntity.ok(
                barberService.getBarbersForCustomer(shopId)
        );
    }

}