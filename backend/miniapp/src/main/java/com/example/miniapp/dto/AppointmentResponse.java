package com.example.miniapp.dto;

import com.example.miniapp.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID clientId,
        UUID providerId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status
) {}