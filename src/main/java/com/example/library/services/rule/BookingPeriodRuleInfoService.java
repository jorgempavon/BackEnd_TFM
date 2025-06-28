package com.example.library.services.rule;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.repository.rule.BookingPeriodRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingPeriodRuleInfoService {
    private final BookingPeriodRuleRepository bookingPeriodRuleRepository;
    private final RuleInfoService ruleInfoService;

    public BookingPeriodRuleInfoService(BookingPeriodRuleRepository bookingPeriodRuleRepository,RuleInfoService ruleInfoService){
        this.bookingPeriodRuleRepository = bookingPeriodRuleRepository;
        this.ruleInfoService = ruleInfoService;
    }


    public RuleExistenceDTO findByNumPenalties(Integer numPenalties){
        RuleExistenceDTO ruleExistenceDTO = this.ruleInfoService.findByNumPenalties(numPenalties);

        if (!ruleExistenceDTO.getExistsRule()){
            return ruleExistenceDTO;
        }
        RuleDTO ruleDTO = ruleExistenceDTO.getRuleDTO();
        if (!this.bookingPeriodRuleRepository.existsByRuleId(ruleDTO.getId())){
            return new RuleExistenceDTO(false,null);
        }
        return ruleExistenceDTO;
    }

    public BookingPeriodRule findById(Long id){
        if (!this.bookingPeriodRuleRepository.existsById(id)){
            throw new NotFoundException("No existe la regla con el id proporcionado");
        }
        return this.bookingPeriodRuleRepository.findById(id).get();
    }
}
