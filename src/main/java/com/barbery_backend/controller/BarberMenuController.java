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

    @GetMapping
    public ResponseEntity<List<BarberMenu>> getAllServices() {
        return ResponseEntity.ok(barberMenuService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberMenu> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(barberMenuService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<BarberMenu> createService(@RequestBody BarberMenuRequest request) {
        BarberMenu createdService = barberMenuService.createService(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberMenu> updateService(@PathVariable Long id, @RequestBody BarberMenuRequest request) {
        return ResponseEntity.ok(barberMenuService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        barberMenuService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}