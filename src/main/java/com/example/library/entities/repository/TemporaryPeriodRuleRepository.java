package com.example.library.entities.repository;

import com.example.library.entities.model.TemporaryPeriodRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemporaryPeriodRuleRepository extends JpaRepository<TemporaryPeriodRule, Long>, JpaSpecificationExecutor<TemporaryPeriodRule> {
}
