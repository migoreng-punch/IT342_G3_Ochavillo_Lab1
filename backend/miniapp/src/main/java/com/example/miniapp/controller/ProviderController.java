package com.example.miniapp.controller;

import com.example.miniapp.entity.User;
import com.example.miniapp.repository.UserRepository;
import com.example.miniapp.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public ProviderController(AppointmentService appointmentService,
                              UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam LocalDate date) {

        User provider = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        return ResponseEntity.ok(
                appointmentService.generateAvailableSlots(provider, date)
        );
    }
}
