package com.example.library.services.rule;

import com.example.library.entities.dto.rule.RuleAndRuleDTO;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.repository.rule.BookingPeriodRuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BookingPeriodRuleService {
    private final BookingPeriodRuleRepository bookingPeriodRuleRepository;
    private final RuleService ruleService;

    public BookingPeriodRuleService(BookingPeriodRuleRepository bookingPeriodRuleRepository,RuleService ruleService){
        this.bookingPeriodRuleRepository = bookingPeriodRuleRepository;
        this.ruleService = ruleService;
    }
    @Transactional
    public void deleteByRuleId(Long ruleId){
        if(this.bookingPeriodRuleRepository.existsByRuleId(ruleId)){
            BookingPeriodRule bookingPeriodRule = this.bookingPeriodRuleRepository.findByRuleId(ruleId).get();
            this.bookingPeriodRuleRepository.delete(bookingPeriodRule);
        }
        this.ruleService.delete(ruleId);
    }

    @Transactional
    public RuleDTO create(Long creatorUserId,RuleCreateDTO ruleCreateDTO){
        RuleAndRuleDTO ruleAndRuleDTO = this.ruleService.create(creatorUserId,ruleCreateDTO,"Intervalo de reserva");
        Rule rule = ruleAndRuleDTO.getRule();
        RuleDTO ruleDTO = ruleAndRuleDTO.getRuleDTO();

        BookingPeriodRule bookingPeriodRule = new BookingPeriodRule();
        bookingPeriodRule.setRule(rule);
        this.bookingPeriodRuleRepository.save(bookingPeriodRule);

        return ruleDTO;
    }
}
