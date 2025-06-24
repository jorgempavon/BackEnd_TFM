package com.example.library.entities.repository.penalty;

import com.example.library.entities.model.penalty.BookingPeriodPenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingPeriodPenaltyRepository  extends JpaRepository<BookingPeriodPenalty, Long>, JpaSpecificationExecutor<BookingPeriodPenalty> {
}
