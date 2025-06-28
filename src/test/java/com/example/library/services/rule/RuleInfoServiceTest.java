package com.example.library.services.rule;

import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.dto.rule.RuleUpdateDTO;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.rule.RuleRepository;
import com.example.library.services.user.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuleInfoServiceTest {
    @Mock
    private AdminService adminService;
    @Mock
    private RuleRepository ruleRepository;
    @InjectMocks
    private RuleInfoService ruleInfoService;
    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_TYPE = "temporal";
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

    @Test
    void findByNumPenalties_returnsNotExists(){
        when(this.ruleRepository.existsByNumPenalties(RULE_NUM_PENALTIES)).thenReturn(false);
        RuleExistenceDTO response = this.ruleInfoService.findByNumPenalties(RULE_NUM_PENALTIES);

        assertFalse(response.getExistsRule());
    }

    @Test
    void findByNumPenalties_returnsExists(){
        when(this.ruleRepository.existsByNumPenalties(RULE_NUM_PENALTIES)).thenReturn(true);
        java.util.List<Rule> mockRules = List.of(RULE);
        when(this.ruleRepository.findByNumPenalties(RULE_NUM_PENALTIES)).thenReturn(Optional.of(mockRules));
        when(this.adminService.getUserFullNameByAdmin(ADMIN)).thenReturn(ADMIN_FULL_NAME);

        RuleExistenceDTO response = this.ruleInfoService.findByNumPenalties(RULE_NUM_PENALTIES);
        RuleDTO ruleDTO = response.getRuleDTO();

        assertTrue(response.getExistsRule());
        assertEquals(ruleDTO.getName(),RULE_NAME);
        assertEquals(ruleDTO.getDays(),RULE_DAYS);
        assertEquals(ruleDTO.getNumPenalties(),RULE_NUM_PENALTIES);

    }

}
