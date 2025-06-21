package com.example.library.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemporaryPeriodRuleRepository extends JpaRepository<TemporaryPeriodRuleRepository, Long>, JpaSpecificationExecutor<TemporaryPeriodRuleRepository> {
}
