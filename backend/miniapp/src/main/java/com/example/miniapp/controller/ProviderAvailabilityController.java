package com.example.miniapp.controller;

import com.example.miniapp.dto.CreateWeeklyAvailabilityRequest;
import com.example.miniapp.entity.User;
import com.example.miniapp.repository.UserRepository;
import com.example.miniapp.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/availability")
public class ProviderAvailabilityController {

    private final AppointmentService availabilityService;
    private final UserRepository userRepository;

    public ProviderAvailabilityController(
            AppointmentService availabilityService,
            UserRepository userRepository) {
        this.availabilityService = availabilityService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createAvailability(
            @AuthenticationPrincipal String username,
            @RequestBody CreateWeeklyAvailabilityRequest request) {

        User provider = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        availabilityService.createWeeklyAvailability(
                provider,
                request.dayOfWeek(),
                request.startTime(),
                request.endTime()
        );

        return ResponseEntity.ok("Weekly availability created.");
    }
}
