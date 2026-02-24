package com.example.miniapp.repository;

import com.example.miniapp.entity.ProviderWeeklyAvailability;
import com.example.miniapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface ProviderWeeklyAvailabilityRepository
        extends JpaRepository<ProviderWeeklyAvailability, UUID> {

    List<ProviderWeeklyAvailability>
    findByProviderAndDayOfWeek(User provider, DayOfWeek dayOfWeek);
}
