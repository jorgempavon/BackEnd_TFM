package com.example.library.entities.repository;

import com.example.library.entities.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {
    boolean existsByNameAndNumPenaltiesAndDaysAndType(String name, Integer numPenalties, Integer days,String type);
    boolean existsByNameAndNumPenaltiesAndDaysAndTypeAndNotId(String name, Integer numPenalties, Integer days,String type,Long id);

    Optional<Rule> findByNameAndNumPenaltiesAndDays(String name, Integer numPenalties, Integer days);
}
