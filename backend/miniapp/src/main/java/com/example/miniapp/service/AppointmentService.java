package com.example.miniapp.service;

import com.example.miniapp.entity.Appointment;
import com.example.miniapp.entity.ProviderWeeklyAvailability;
import com.example.miniapp.entity.User;
import com.example.miniapp.enums.AppointmentStatus;
import com.example.miniapp.repository.AppointmentRepository;
import com.example.miniapp.repository.ProviderWeeklyAvailabilityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ProviderWeeklyAvailabilityRepository availabilityRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ProviderWeeklyAvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public void bookAppointment(User client,
                                User provider,
                                LocalDate date,
                                LocalTime start) {

        List<LocalTime> validSlots = generateAvailableSlots(provider, date);

        int duration = provider.getSlotDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (!validSlots.contains(start)) {
            throw new RuntimeException("Selected slot is not available.");
        }

        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        if (date.isBefore(today)) {
            throw new RuntimeException("Cannot book an appointment in the past.");
        }

        if (date.isEqual(today) && start.isBefore(nowTime)) {
            throw new RuntimeException("Cannot book a past time slot.");
        }

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setProvider(provider);
        appointment.setAppointmentDate(date);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setStatus(AppointmentStatus.PENDING);

        appointmentRepository.save(appointment);
    }

    private void validateWeeklyAvailability(User provider,
                                            LocalDate date,
                                            LocalTime start,
                                            LocalTime end) {

        DayOfWeek day = date.getDayOfWeek();

        List<ProviderWeeklyAvailability> schedules =
                availabilityRepository.findByProviderAndDayOfWeek(provider, day);

        boolean valid = schedules.stream().anyMatch(schedule ->
                !start.isBefore(schedule.getStartTime()) &&
                        !end.isAfter(schedule.getEndTime())
        );

        if (!valid) {
            throw new RuntimeException("Selected time is outside provider availability.");
        }
    }

    @Transactional
    public void createWeeklyAvailability(User provider,
                                         DayOfWeek day,
                                         LocalTime start,
                                         LocalTime end) {

        if (!provider.getRole().equals("PROVIDER")) {
            throw new RuntimeException("Only providers can set availability.");
        }

        if (!start.isBefore(end)) {
            throw new RuntimeException("Start time must be before end time.");
        }

        validateNoOverlap(provider, day, start, end);

        ProviderWeeklyAvailability availability = new ProviderWeeklyAvailability();
        availability.setProvider(provider);
        availability.setDayOfWeek(day);
        availability.setStartTime(start);
        availability.setEndTime(end);

        availabilityRepository.save(availability);
    }

    public List<LocalTime> generateAvailableSlots(User provider, LocalDate date) {

        if (provider.getSlotDurationMinutes() == null) {
            throw new RuntimeException("Provider slot duration not configured.");
        }

        DayOfWeek day = date.getDayOfWeek();

        List<ProviderWeeklyAvailability> schedules =
                availabilityRepository.findByProviderAndDayOfWeek(provider, day);

        if (schedules.isEmpty()) {
            return List.of();
        }

        List<Appointment> existingAppointments =
                appointmentRepository
                        .findByProviderAndAppointmentDateAndStatusIn(
                                provider,
                                date,
                                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                        );

        Set<LocalTime> booked =
                existingAppointments.stream()
                        .map(Appointment::getStartTime)
                        .collect(Collectors.toSet());

        List<LocalTime> availableSlots = new ArrayList<>();
        int duration = provider.getSlotDurationMinutes();

        for (ProviderWeeklyAvailability schedule : schedules) {

            LocalTime current = schedule.getStartTime();

            while (!current.plusMinutes(duration).isAfter(schedule.getEndTime())) {

                if (!booked.contains(current)) {
                    availableSlots.add(current);
                }

                current = current.plusMinutes(duration);
            }
        }

        return availableSlots;
    }

    private void validateNoOverlap(User provider,
                                   DayOfWeek day,
                                   LocalTime start,
                                   LocalTime end) {

        List<ProviderWeeklyAvailability> existing =
                availabilityRepository.findByProviderAndDayOfWeek(provider, day);

        boolean overlaps = existing.stream().anyMatch(e ->
                start.isBefore(e.getEndTime()) &&
                        end.isAfter(e.getStartTime())
        );

        if (overlaps) {
            throw new RuntimeException("Availability overlaps existing schedule.");
        }
    }

    @Transactional
    public void confirmAppointment(UUID appointmentId, User provider) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        if (!appointment.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized to confirm this appointment.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be confirmed.");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
    }

    @Transactional
    public void cancelAppointment(UUID appointmentId, User requester) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        boolean isClient =
                appointment.getClient().getId().equals(requester.getId());

        boolean isProvider =
                appointment.getProvider().getId().equals(requester.getId());

        if (!isClient && !isProvider) {
            throw new RuntimeException("Unauthorized to cancel this appointment.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getAppointmentsForClient(User client) {
        return appointmentRepository.findByClient(client);
    }

    public List<Appointment> getAppointmentsForProvider(User provider) {
        return appointmentRepository.findByProvider(provider);
    }

}