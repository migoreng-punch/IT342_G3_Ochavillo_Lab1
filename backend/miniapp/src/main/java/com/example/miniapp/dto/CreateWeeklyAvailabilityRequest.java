package com.example.miniapp.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CreateWeeklyAvailabilityRequest(
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) { }
