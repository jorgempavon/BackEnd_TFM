package com.example.library.entities.repository.penalty;

import com.example.library.entities.model.penalty.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PenaltyRepository extends JpaRepository<Penalty, Long>, JpaSpecificationExecutor<Penalty> {
}
