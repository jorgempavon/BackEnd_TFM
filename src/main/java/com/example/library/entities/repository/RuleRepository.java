package com.example.library.entities.repository;

import com.example.library.entities.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RuleRepository extends JpaRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {
}
