package com.example.library.resources.rule;

import com.example.library.api.resources.rule.RuleResource;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleUpdateDTO;
import com.example.library.services.rule.RuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuleResourceTest {
    @Mock
    private RuleService ruleService;
    @InjectMocks
    private RuleResource ruleResource;
    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_OTHER_NAME = "other rule name";
    private static final Integer RULE_OTHER_NUM_PENALTIES = 15;
    private static final Integer RULE_OTHER_DAYS = 2;
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
    private static final List<RuleDTO> LIST_RULES_DTO = List.of(RULE_DTO);
    @Test
    void findRuleByIdSuccessful(){
        when(this.ruleService.findById(RULE_ID)).thenReturn(RULE_DTO);
        ResponseEntity<?> result = ruleResource.findById(RULE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof RuleDTO);
    }
    @Test
    void findByNameAndDniAndEmailSuccessful(){
        when(this.ruleService.findByNameAndNumMimPenalties(RULE_NAME,RULE_NUM_PENALTIES)).thenReturn(LIST_RULES_DTO);

        ResponseEntity<?> result = ruleResource.findByNameAndNumMimPenalties(RULE_NAME,RULE_NUM_PENALTIES);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),LIST_RULES_DTO);
    }
    @Test
    void updateRuleSuccessful(){
        RuleUpdateDTO ruleUpdateDTO = new RuleUpdateDTO(
                RULE_OTHER_NAME,RULE_OTHER_NUM_PENALTIES,RULE_OTHER_DAYS
        );
        when(this.ruleService.update(RULE_ID,ruleUpdateDTO)).thenReturn(RULE_DTO);

        ResponseEntity<?> result = ruleResource.update(RULE_ID,ruleUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),RULE_DTO);
    }
}
