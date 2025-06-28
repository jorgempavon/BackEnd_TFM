package com.example.library.services.rule;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.repository.rule.TemporaryPeriodRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class TemporaryPeriodRuleInfoService {
    private final TemporaryPeriodRuleRepository temporaryPeriodRuleRepository;
    private final RuleInfoService ruleInfoService;

    public TemporaryPeriodRuleInfoService(TemporaryPeriodRuleRepository temporaryPeriodRuleRepository,
                                          RuleInfoService ruleInfoService){
        this.temporaryPeriodRuleRepository = temporaryPeriodRuleRepository;
        this.ruleInfoService = ruleInfoService;
    }

    public RuleExistenceDTO findByNumPenalties(Integer numPenalties){
        RuleExistenceDTO ruleExistenceDTO = this.ruleInfoService.findByNumPenalties(numPenalties);

        if (!ruleExistenceDTO.getExistsRule()){
            return ruleExistenceDTO;
        }
        RuleDTO ruleDTO = ruleExistenceDTO.getRuleDTO();
        if (!this.temporaryPeriodRuleRepository.existsByRuleId(ruleDTO.getId())){
            return new RuleExistenceDTO(false,null);
        }
        return ruleExistenceDTO;
    }

    public TemporaryPeriodRule findById(Long id){
        if (!this.temporaryPeriodRuleRepository.existsById(id)){
            throw new NotFoundException("No existe la regla con el id proporcionado");
        }
        return this.temporaryPeriodRuleRepository.findById(id).get();
    }

}
