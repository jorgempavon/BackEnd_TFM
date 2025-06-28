package com.example.library.entities.repository.rule;

import com.example.library.entities.model.rule.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long>, JpaSpecificationExecutor<Rule> {
    boolean existsByNameAndNumPenaltiesAndDaysAndType(String name, Integer numPenalties, Integer days,String type);
    boolean existsByNameAndNumPenaltiesAndDaysAndTypeAndIdNot(String name, Integer numPenalties, Integer days,String type,Long id);
    boolean existsByNumPenalties(Integer numPenalties);
    Optional<List<Rule>> findByNumPenalties(Integer numPenalties);
    Optional<Rule> findByNameAndNumPenaltiesAndDays(String name, Integer numPenalties, Integer days);
}
