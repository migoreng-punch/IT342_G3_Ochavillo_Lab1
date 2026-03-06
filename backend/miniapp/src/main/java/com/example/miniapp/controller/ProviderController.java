package com.example.miniapp.controller;

import com.example.miniapp.entity.Establishment;
import com.example.miniapp.entity.User;
import com.example.miniapp.repository.EstablishmentRepository;
import com.example.miniapp.repository.UserRepository;
import com.example.miniapp.service.AppointmentService;
import com.example.miniapp.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final AvailabilityService availabilityService;
    private final EstablishmentRepository establishmentRepository;

    public ProviderController(AvailabilityService availabilityService,
                              EstablishmentRepository establishmentRepository) {
        this.availabilityService = availabilityService;
        this.establishmentRepository = establishmentRepository;
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam LocalDate date) {

        Establishment establishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establishment not found"));

        return ResponseEntity.ok(
                availabilityService.generateAvailableSlots(establishment, date)
        );
    }
}
