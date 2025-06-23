package com.example.library.entities.repository;

import com.example.library.entities.model.BookingPeriodRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookingPeriodRuleRepository extends JpaRepository<BookingPeriodRule, Long>, JpaSpecificationExecutor<BookingPeriodRule> {

    boolean existsByRuleId(Long ruleId);

    Optional<BookingPeriodRule> findByRuleId(Long ruleId);
}
