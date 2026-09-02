package com.barbery_backend.controller;

import com.barbery_backend.dto.BarberMenuRequest;
import com.barbery_backend.entity.BarberMenu;
import com.barbery_backend.service.BarberMenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barber-menu")
public class BarberMenuController {

    private final BarberMenuService barberMenuService;

    public BarberMenuController(BarberMenuService barberMenuService) {
        this.barberMenuService = barberMenuService;
    }

    // Admin - Get all barber menu items for a shop
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<BarberMenu>> getServicesByShop(
            @PathVariable Long shopId
    ) {
        return ResponseEntity.ok(
                barberMenuService.getServicesByShop(shopId)
        );
    }

    // Customer - Get only active barber menu items for a shop
    @GetMapping("/shop/{shopId}/customer")
    public ResponseEntity<List<BarberMenu>> getActiveServicesByShop(
            @PathVariable Long shopId
    ) {
        return ResponseEntity.ok(
                barberMenuService.getActiveServicesByShop(shopId)
        );
    }

    // Get a single barber menu item
    @GetMapping("/{id}")
    public ResponseEntity<BarberMenu> getServiceById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                barberMenuService.getServiceById(id)
        );
    }

    // Create a new barber menu item
    @PostMapping
    public ResponseEntity<BarberMenu> createService(
            @RequestBody BarberMenuRequest request
    ) {
        BarberMenu createdService =
                barberMenuService.createService(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdService);
    }

    // Update barber menu item
    @PutMapping("/{id}")
    public ResponseEntity<BarberMenu> updateService(
            @PathVariable Long id,
            @RequestBody BarberMenuRequest request
    ) {
        return ResponseEntity.ok(
                barberMenuService.updateService(id, request)
        );
    }

    // Soft delete / deactivate barber menu item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long id
    ) {
        barberMenuService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}