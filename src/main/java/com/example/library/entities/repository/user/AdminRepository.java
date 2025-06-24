package com.example.library.entities.repository.user;

import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUser(User user);

    Optional<Admin> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
