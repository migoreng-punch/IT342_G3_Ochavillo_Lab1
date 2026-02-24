package com.example.miniapp.repository;

import com.example.miniapp.entity.Appointment;
import com.example.miniapp.entity.User;
import com.example.miniapp.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    List<Appointment>
    findByProviderAndAppointmentDate(User provider, LocalDate date);

    List<Appointment>
    findByClient(User client);

    List<Appointment>
    findByProvider(User provider);

    List<Appointment> findByProviderAndAppointmentDateAndStatusIn(
            User provider,
            LocalDate date,
            List<AppointmentStatus> statuses
    );
}