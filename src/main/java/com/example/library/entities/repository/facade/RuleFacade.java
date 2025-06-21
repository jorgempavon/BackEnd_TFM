package com.example.library.facade;

import com.example.library.entities.model.*;
import com.example.library.entities.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RuleFacade {

    private final RuleRepository ruleRepository;
    private final BookingPeriodRuleRepository bookingPeriodRuleRepository;
    private final TemporaryPeriodRuleRepository temporaryPeriodRuleRepository;

    public RuleFacade(RuleRepository ruleRepository, BookingPeriodRuleRepository bookingPeriodRuleRepository,
                      TemporaryPeriodRuleRepository temporaryPeriodRuleRepository) {
        this.ruleRepository = ruleRepository;
        this.bookingPeriodRuleRepository = bookingPeriodRuleRepository;
        this.temporaryPeriodRuleRepository = temporaryPeriodRuleRepository;
    }

}
