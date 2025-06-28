package com.example.library.services.rule;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.rule.TemporaryPeriodRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemporaryPeriodRuleInfoServiceTest {
    @Mock
    private TemporaryPeriodRuleRepository temporaryPeriodRuleRepository;
    @Mock
    private RuleInfoService ruleInfoService;
    @InjectMocks
    private TemporaryPeriodRuleInfoService temporaryPeriodRuleInfoService;
    private static final Integer NUM_PENALTIES = 8;
    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_TYPE = "temporal";
    private static final String ADMIN_FULL_NAME = "Nombre completo admin";
    private static final Long TEMPORARY_PERIOD_RULE_ID = 4L;

    private static final String USER_NAME = "user name";
    private static final String USER_LAST_NAME = "User last name";
    private static final String USER_DNI = "11112222Y";
    private static final String USER_EMAIL = "test@example.com";

    private static final User USER = new User(
            USER_NAME,
            USER_DNI,
            USER_EMAIL,
            USER_LAST_NAME
    );
    private static final Long ADMIN_ID = 5L;
    private static final Admin ADMIN = new Admin(
            ADMIN_ID,
            USER
    );
    private static final Rule RULE = new Rule(
            RULE_ID,
            RULE_NAME,
            RULE_NUM_PENALTIES,
            RULE_DAYS,
            ADMIN,
            RULE_TYPE

    );
    private static final TemporaryPeriodRule TEMPORARY_PERIOD_RULE = new TemporaryPeriodRule(
            TEMPORARY_PERIOD_RULE_ID,RULE
    );

    private static final RuleDTO RULE_DTO = new RuleDTO(
            RULE_ID,
            RULE_NUM_PENALTIES,
            RULE_DAYS,
            RULE_NAME,
            ADMIN_FULL_NAME,
            RULE_TYPE

    );
    private static final RuleExistenceDTO RULE_EXISTENCE_DTO = new RuleExistenceDTO(true,RULE_DTO);
    @Test
    void findByNumPenalties_NotExistsRuleTemporary(){
        RuleExistenceDTO dto = new RuleExistenceDTO(false,null);
        when(this.ruleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(dto);
        RuleExistenceDTO response = this.temporaryPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES);
        assertFalse(response.getExistsRule());
    }
    @Test
    void findByNumPenalties_NotExistsTemporaryRule(){
        when(this.ruleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(RULE_EXISTENCE_DTO);
        when(this.temporaryPeriodRuleRepository.existsByRuleId(RULE_ID)).thenReturn(false);

        RuleExistenceDTO response = this.temporaryPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES);
        assertFalse(response.getExistsRule());
    }

    @Test
    void findByNumPenalties_ExistsTemporary(){
        when(this.ruleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(RULE_EXISTENCE_DTO);
        when(this.temporaryPeriodRuleRepository.existsByRuleId(RULE_ID)).thenReturn(true);

        RuleExistenceDTO response = this.temporaryPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES);
        assertTrue(response.getExistsRule());
    }

    @Test
    void findByIdBookingPeriodRule_successful(){
        when(this.temporaryPeriodRuleRepository.existsById(TEMPORARY_PERIOD_RULE_ID)).thenReturn(true);
        when(this.temporaryPeriodRuleRepository.findById(TEMPORARY_PERIOD_RULE_ID)).thenReturn(Optional.of(TEMPORARY_PERIOD_RULE));

        TemporaryPeriodRule response = this.temporaryPeriodRuleInfoService.findById(TEMPORARY_PERIOD_RULE_ID);
        assertEquals(response.getId(),TEMPORARY_PERIOD_RULE_ID);
        assertEquals(response.getRule(),RULE);
    }

    @Test
    void findByIdBookingPeriodRule_whenNotExists_throwNotFoundException(){
        when(this.temporaryPeriodRuleRepository.existsById(TEMPORARY_PERIOD_RULE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            temporaryPeriodRuleInfoService.findById(TEMPORARY_PERIOD_RULE_ID);
        });
    }
}
