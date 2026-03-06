package com.example.miniapp.controller;

import com.example.miniapp.dto.CreateWeeklyAvailabilityRequest;
import com.example.miniapp.entity.Establishment;
import com.example.miniapp.entity.User;
import com.example.miniapp.repository.EstablishmentRepository;
import com.example.miniapp.repository.UserRepository;
import com.example.miniapp.service.AppointmentService;
import com.example.miniapp.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/availability")
public class WeeklyAvailabilityController {

    private final AvailabilityService availabilityService;
    private final UserRepository userRepository;
    private final EstablishmentRepository establishmentRepository;

    public WeeklyAvailabilityController(
            AvailabilityService availabilityService,
            UserRepository userRepository,
            EstablishmentRepository establishmentRepository) {
        this.availabilityService = availabilityService;
        this.userRepository = userRepository;
        this.establishmentRepository = establishmentRepository;
    }

    @PostMapping
    public ResponseEntity<?> createAvailability(
            @AuthenticationPrincipal String username,
            @RequestBody CreateWeeklyAvailabilityRequest request) {

        User provider = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Establishment establishment = establishmentRepository
                .findByOwner(provider)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));

        availabilityService.createWeeklyAvailability(
                provider,
                establishment,
                request.dayOfWeek(),
                request.startTime(),
                request.endTime()
        );

        return ResponseEntity.ok("Weekly availability created.");
    }
}
