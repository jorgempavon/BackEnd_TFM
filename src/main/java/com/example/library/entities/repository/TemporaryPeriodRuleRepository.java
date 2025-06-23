package com.example.library.entities.repository;

import com.example.library.entities.model.TemporaryPeriodRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryPeriodRuleRepository extends JpaRepository<TemporaryPeriodRule, Long>{
    boolean existsByRuleId(Long ruleId);

    Optional<TemporaryPeriodRule> findByRuleId(Long ruleId);
}
