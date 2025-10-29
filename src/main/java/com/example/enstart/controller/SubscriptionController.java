package com.example.enstart.controller;

import com.example.enstart.service.SubscriptionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/subscription")
    public ResponseEntity<String> subscribe(@RequestBody SubscriptionRequest request) {
        try {
            subscriptionService.createSubscription(request.getEmail(), request.getDomain());
            return ResponseEntity.status(HttpStatus.CREATED).body("Subscription successful.");
        } catch (RuntimeException e) {
            // A more sophisticated error handling can be implemented here
            if (e.getMessage().contains("Domain not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("Already subscribed")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @Getter
    @Setter
    public static class SubscriptionRequest {
        private String email;
        private String domain;
    }
}
