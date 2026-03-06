package com.example.miniapp.repository;

import com.example.miniapp.entity.Establishment;
import com.example.miniapp.entity.WeeklyAvailability;
import com.example.miniapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface WeeklyAvailabilityRepository
        extends JpaRepository<WeeklyAvailability, UUID> {

    List<WeeklyAvailability>
    findByEstablishmentAndDayOfWeek(Establishment establishment, DayOfWeek dayOfWeek);
}
