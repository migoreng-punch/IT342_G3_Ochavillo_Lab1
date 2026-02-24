package com.example.miniapp.controller;

import com.example.miniapp.dto.BookAppointmentRequest;
import com.example.miniapp.entity.User;
import com.example.miniapp.repository.UserRepository;
import com.example.miniapp.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
    }

    // ✅ Book Appointment (Client)
    @PostMapping
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal String username,
            @RequestBody BookAppointmentRequest request) {

        User client = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User provider = userRepository.findById(request.providerId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        appointmentService.bookAppointment(
                client,
                provider,
                request.date(),
                request.startTime()
        );

        return ResponseEntity.ok("Appointment booked. Awaiting confirmation.");
    }

    // ✅ Confirm Appointment (Provider Only)
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {

        User provider = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        appointmentService.confirmAppointment(id, provider);

        return ResponseEntity.ok("Appointment confirmed.");
    }

    // ❌ Cancel Appointment (Client or Provider)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable UUID id,
            @AuthenticationPrincipal String username) {

        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        appointmentService.cancelAppointment(id, requester);

        return ResponseEntity.ok("Appointment cancelled.");
    }

    // 📋 Get My Appointments (Client)
    @GetMapping("/my")
    public ResponseEntity<?> getMyAppointments(
            @AuthenticationPrincipal(expression = "username") String username) {

        User client = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                appointmentService.getAppointmentsForClient(client)
        );
    }

    // 📋 Get Provider Appointments
    @GetMapping("/provider")
    public ResponseEntity<?> getProviderAppointments(
            @AuthenticationPrincipal(expression = "username") String username) {

        User provider = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                appointmentService.getAppointmentsForProvider(provider)
        );
    }
}