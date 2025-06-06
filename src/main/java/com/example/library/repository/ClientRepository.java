package com.example.library.repository;

import com.example.library.model.Client;
import com.example.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUser(User user);

    Optional<Client> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
