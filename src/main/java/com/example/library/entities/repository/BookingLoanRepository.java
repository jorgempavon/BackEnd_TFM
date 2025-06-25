package com.example.library.entities.repository;

import com.example.library.entities.model.BookingLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingLoanRepository extends JpaRepository<BookingLoan, Long>, JpaSpecificationExecutor<BookingLoan> {
}
