package com.barbery_backend.controller;

import com.barbery_backend.entity.User;
import com.barbery_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Existing API: Create User
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }


    // New API: Find Customer by Phone
    @GetMapping("/by-phone")
    public ResponseEntity<Map<String, Object>> getCustomerByPhone(
            @RequestParam String phone) {

        return userService.getCustomerByPhone(phone)
                .map(user -> ResponseEntity.ok(
                        Map.<String, Object>of(
                                "exists", true,
                                "customerId", user.getId(),
                                "name", user.getName()
                        )
                ))
                .orElseGet(() -> ResponseEntity.ok(
                        Map.<String, Object>of(
                                "exists", false,
                                "message", "Customer not found"
                        )
                ));
    }
}