package com.example.library.entities.repository;

import com.example.library.entities.model.BookingLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BookingLoanRepository extends JpaRepository<BookingLoan, Long>, JpaSpecificationExecutor<BookingLoan> {

    boolean existsByClientId(Long clientId);
    Optional<List<BookingLoan>> findByClientId(Long clientId);
}
