package com.barbery_backend.controller;

import com.barbery_backend.dto.JoinQueueRequest;
import com.barbery_backend.dto.QueueResponse;
import com.barbery_backend.entity.CurrentQueue;
import com.barbery_backend.service.JoinQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "http://localhost:4200")
public class JoinQueueController {

    private final JoinQueueService joinQueueService;

    public JoinQueueController(JoinQueueService joinQueueService) {
        this.joinQueueService = joinQueueService;
    }

    @PostMapping("/join")
    public ResponseEntity<CurrentQueue> joinQueue(
            @RequestBody JoinQueueRequest request
    ) {
        CurrentQueue queueEntry = joinQueueService.joinQueue(request);

        return ResponseEntity.ok(queueEntry);
    }


    @DeleteMapping("/clear")
    public ResponseEntity<String> clearQueue() {

        joinQueueService.clearQueue();

        return ResponseEntity.ok("Current queue cleared successfully");
    }


    @GetMapping("/shop/{shopId}/barber/{barberId}")
    public ResponseEntity<List<QueueResponse>> getQueueByShopAndBarber(
            @PathVariable Long shopId,
            @PathVariable Long barberId
    ) {
        List<QueueResponse> queue =
                joinQueueService.getQueueByShopAndBarber(shopId, barberId);

        return ResponseEntity.ok(queue);
    }

}