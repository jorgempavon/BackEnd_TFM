package com.example.library.services.rule;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleAndRuleDTO;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.repository.rule.TemporaryPeriodRuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TemporaryPeriodRuleService {
    private final TemporaryPeriodRuleRepository temporaryPeriodRuleRepository;
    private final RuleService ruleService;

    public TemporaryPeriodRuleService(TemporaryPeriodRuleRepository temporaryPeriodRuleRepository,RuleService ruleService){
        this.temporaryPeriodRuleRepository = temporaryPeriodRuleRepository;
        this.ruleService = ruleService;
    }
    @Transactional
    public void deleteByRuleId(Long ruleId){
        if(this.temporaryPeriodRuleRepository.existsByRuleId(ruleId)){
            TemporaryPeriodRule temporaryPeriodRule = this.temporaryPeriodRuleRepository.findByRuleId(ruleId).get();
            this.temporaryPeriodRuleRepository.delete(temporaryPeriodRule);
        }
        this.ruleService.delete(ruleId);
    }

    @Transactional
    public RuleDTO create(Long creatorUserId,RuleCreateDTO ruleCreateDTO){
        RuleAndRuleDTO ruleAndRuleDTO = this.ruleService.create(creatorUserId,ruleCreateDTO,"temporal");
        Rule rule = ruleAndRuleDTO.getRule();
        RuleDTO ruleDTO = ruleAndRuleDTO.getRuleDTO();

        TemporaryPeriodRule temporaryPeriodRule = new TemporaryPeriodRule();
        temporaryPeriodRule.setRule(rule);
        this.temporaryPeriodRuleRepository.save(temporaryPeriodRule);

        return ruleDTO;
    }

    public String getRuleNameByTemporaryPeriodRule(TemporaryPeriodRule temporaryPeriodRule){
        if(!this.temporaryPeriodRuleRepository.existsById(temporaryPeriodRule.getId())){
            throw new NotFoundException("No existe la regla temporal con el id proporcionado");
        }
        return this.ruleService.getRuleNameByRule(temporaryPeriodRule.getRule());
    }
}
