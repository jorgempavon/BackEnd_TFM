package com.example.library.entities.repository;

import com.example.library.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByDni(String dni);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}
