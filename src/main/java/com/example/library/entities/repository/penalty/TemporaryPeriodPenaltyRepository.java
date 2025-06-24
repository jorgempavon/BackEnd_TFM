package com.example.library.entities.repository.penalty;

import com.example.library.entities.model.penalty.TemporaryPeriodPenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TemporaryPeriodPenaltyRepository  extends JpaRepository<TemporaryPeriodPenalty, Long>, JpaSpecificationExecutor<TemporaryPeriodPenalty> {
    boolean existsByPenaltyId(Long penaltyId);
    Optional<TemporaryPeriodPenalty> findByPenaltyId(Long penaltyId);
}
