package com.example.library.entities.repository;

import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUser(User user);

    Optional<Client> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
