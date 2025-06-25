package com.example.library.resources.rule;

import com.example.library.api.resources.rule.BookingPeriodRuleResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.services.rule.BookingPeriodRuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingPeriodRuleResourceTest {
    @Mock
    private BookingPeriodRuleService bookingPeriodRuleService;
    @Mock
    private CustomUserDetails mockUserDetails;
    @InjectMocks
    private BookingPeriodRuleResource bookingPeriodRuleResource;

    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_TYPE = "temporal";
    private static final String ADMIN_FULL_NAME = "Nombre completo admin";
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

    @Test
    void createBookingPeriodRule_successful() {
        when(this.bookingPeriodRuleService.create(RULE_ID,RULE_CREATE_DTO)).thenReturn(RULE_DTO);
        when(this.mockUserDetails.getId()).thenReturn(RULE_ID);

        ResponseEntity<?> result = bookingPeriodRuleResource.create(mockUserDetails,RULE_CREATE_DTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof RuleDTO);

        RuleDTO resultDto = (RuleDTO) result.getBody();
        assertEquals(resultDto.getName(), RULE_NAME);
        assertEquals(resultDto.getNumPenalties(), RULE_NUM_PENALTIES);
        assertEquals(resultDto.getDays(), RULE_DAYS);
    }

    @Test
    void deleteBookingPeriodRule_successful(){
        doNothing().when(this.bookingPeriodRuleService).deleteByRuleId(RULE_ID);
        ResponseEntity<?> result = bookingPeriodRuleResource.delete(RULE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
