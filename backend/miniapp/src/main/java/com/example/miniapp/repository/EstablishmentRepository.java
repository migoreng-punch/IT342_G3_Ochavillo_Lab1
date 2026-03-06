package com.example.miniapp.repository;

import com.example.miniapp.entity.Establishment;
import com.example.miniapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    Optional<Establishment> findById(Long id);
    Optional<Establishment> findByOwnerId(Long id);
    Optional<Establishment> findByOwner(User owner);
    boolean existsByName(String name);
    boolean existsByContactEmail(String email);
}
