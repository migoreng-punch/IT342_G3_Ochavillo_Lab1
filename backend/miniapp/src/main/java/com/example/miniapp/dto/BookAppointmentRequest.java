package com.example.miniapp.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record BookAppointmentRequest(
        Long providerId,
        LocalDate date,
        LocalTime startTime
) { }
