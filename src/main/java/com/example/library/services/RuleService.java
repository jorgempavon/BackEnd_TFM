package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.entities.dto.BookingPeriodRuleDTO;
import com.example.library.entities.dto.RuleCreateDTO;
import com.example.library.entities.model.Admin;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final RuleFacade ruleFacade;

    public RuleService(RuleFacade ruleFacade){
        this.ruleFacade = ruleFacade;
    }

    public BookingPeriodRuleDTO createBookingPeriodRule(RuleCreateDTO ruleCreateDTO, Admin admin){
        boolean existsRule = this.ruleFacade.existsBookingPeriodRule(ruleCreateDTO.getDays(),ruleCreateDTO.getNumPenalties());

        if(existsRule){
            throw new BadRequestException("Ya existe una regla con el número de días y penalizaciones");
        }

        this.ruleFacade.saveBookingPeriodRule(ruleCreateDTO,admin);
    }
}
