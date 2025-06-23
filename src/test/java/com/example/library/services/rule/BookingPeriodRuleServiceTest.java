package com.example.library.services.rule;

import com.example.library.entities.dto.RuleAndRuleDTO;
import com.example.library.entities.dto.RuleCreateDTO;
import com.example.library.entities.dto.RuleDTO;
import com.example.library.entities.dto.RuleUpdateDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.BookingPeriodRule;
import com.example.library.entities.model.Rule;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.BookingPeriodRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingPeriodRuleServiceTest {
    @Mock
    private RuleService ruleService;
    @Mock
    private BookingPeriodRuleRepository bookingPeriodRuleRepository;
    @InjectMocks
    private BookingPeriodRuleService bookingPeriodRuleService;

    private static final Long RULE_ID = 2L;
    private static final Long BOOKING_PERIOD_RULE_ID = 4L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_TYPE = "Intervalo de reserva";
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
    private static final String ADMIN_FULL_NAME = "Nombre completo admin";
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
    private static final RuleDTO RULE_DTO = new RuleDTO(
            RULE_ID,
            RULE_NUM_PENALTIES,
            RULE_DAYS,
            RULE_NAME,
            ADMIN_FULL_NAME,
            RULE_TYPE

    );
    private static final RuleCreateDTO RULE_CREATE_DTO = new RuleCreateDTO(
            RULE_NAME,RULE_NUM_PENALTIES,RULE_DAYS
    );
    private static final BookingPeriodRule BOOKING_PERIOD_RULE = new BookingPeriodRule(
            BOOKING_PERIOD_RULE_ID,RULE
    );
    @Test
    void createBookingPeriodRule_successful(){
        RuleAndRuleDTO ruleAndRuleDTO = new RuleAndRuleDTO(RULE,RULE_DTO);
        when(this.ruleService.create(RULE_ID,RULE_CREATE_DTO,RULE_TYPE)).thenReturn(ruleAndRuleDTO);

        RuleDTO reponseRuleDTO = this.bookingPeriodRuleService.create(RULE_ID,RULE_CREATE_DTO);

        assertEquals(reponseRuleDTO.getName(),RULE_NAME);
        assertEquals(reponseRuleDTO.getNumPenalties(),RULE_NUM_PENALTIES);
        assertEquals(reponseRuleDTO.getDays(),RULE_DAYS);
        assertEquals(reponseRuleDTO.getType(),RULE_TYPE);
        assertEquals(reponseRuleDTO.getAdminName(),ADMIN_FULL_NAME);
    }

    @Test
    void delete_successful_whenExistsBookingPeriodRule(){
        when(this.bookingPeriodRuleRepository.existsByRuleId(RULE_ID)).thenReturn(true);
        when(this.bookingPeriodRuleRepository.findByRuleId(RULE_ID)).thenReturn(Optional.of(BOOKING_PERIOD_RULE));
        bookingPeriodRuleService.deleteByRuleId(RULE_ID);
    }
    @Test
    void delete_successful_whenNotExistsBookingPeriodRule(){
        when(this.bookingPeriodRuleRepository.existsByRuleId(RULE_ID)).thenReturn(false);
        bookingPeriodRuleService.deleteByRuleId(RULE_ID);
    }
}
