package com.example.library.services.rule;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.rule.RuleAndRuleDTO;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleUpdateDTO;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.rule.RuleRepository;
import com.example.library.services.user.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTest {
    @Mock
    private AdminService adminService;
    @Mock
    private RuleRepository ruleRepository;
    @InjectMocks
    private RuleService ruleService;

    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_OTHER_NAME = "other rule name";
    private static final Integer RULE_OTHER_NUM_PENALTIES = 15;
    private static final Integer RULE_OTHER_DAYS = 2;
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
    private static final RuleUpdateDTO RULE_UPDATE_DTO = new RuleUpdateDTO(
            RULE_OTHER_NAME,RULE_OTHER_NUM_PENALTIES,RULE_OTHER_DAYS
    );
    private static final RuleCreateDTO RULE_CREATE_DTO = new RuleCreateDTO(
            RULE_NAME,RULE_NUM_PENALTIES,RULE_DAYS
    );

    @Test
    void findByIdSuccessful(){
        Rule RULE_IN_FIND = new Rule(RULE_ID,RULE_NAME,RULE_NUM_PENALTIES,RULE_DAYS,ADMIN,RULE_TYPE);
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(true);
        when(this.ruleRepository.findById(RULE_ID)).thenReturn(Optional.of(RULE_IN_FIND));
        when(this.adminService.getUserFullNameByAdmin(any(Admin.class))).thenReturn(ADMIN_FULL_NAME);

        RuleDTO reponseRuleDTO = this.ruleService.findById(RULE_ID);
        assertEquals(reponseRuleDTO.getId(),RULE_ID);
        assertEquals(reponseRuleDTO.getName(),RULE_NAME);
        assertEquals(reponseRuleDTO.getNumPenalties(),RULE_NUM_PENALTIES);
        assertEquals(reponseRuleDTO.getDays(),RULE_DAYS);
        assertEquals(reponseRuleDTO.getType(),RULE_TYPE);
        assertEquals(reponseRuleDTO.getAdminName(),ADMIN_FULL_NAME);
    }

    @Test
    void findByIdWhenNotExistsRuleIdThrowNotFoundException(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            ruleService.findById(RULE_ID);
        });
    }
    
    @Test
    void findByNameAndNumMimPenaltiesSuccessful(){
        List<Rule> mockRules = List.of(RULE);

        when(this.ruleRepository.findAll(any(Specification.class))).thenReturn(mockRules);
        when(this.adminService.getUserFullNameByAdmin(ADMIN)).thenReturn(ADMIN_FULL_NAME);

        List<RuleDTO> result = ruleService.findByNameAndNumMimPenalties(RULE_NAME, RULE_NUM_PENALTIES);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(),RULE_ID);
        assertEquals(result.get(0).getName(),RULE_NAME);
        assertEquals(result.get(0).getNumPenalties(),RULE_NUM_PENALTIES);
        assertEquals(result.get(0).getDays(),RULE_DAYS);
        assertEquals(result.get(0).getType(),RULE_TYPE);
        assertEquals(result.get(0).getAdminName(),ADMIN_FULL_NAME);
    }

    @Test
    void updateRuleSuccessful(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(true);
        when(this.ruleRepository.findById(RULE_ID)).thenReturn(Optional.of(RULE));
        when(this.ruleRepository.existsByNameAndNumPenaltiesAndDaysAndTypeAndIdNot(
                RULE_OTHER_NAME,RULE_OTHER_NUM_PENALTIES,RULE_OTHER_DAYS,RULE_TYPE,RULE_ID
        )).thenReturn(false);
        when(this.adminService.getUserFullNameByAdmin(any(Admin.class))).thenReturn(ADMIN_FULL_NAME);

        RuleDTO reponseRuleDTO = this.ruleService.update(RULE_ID,RULE_UPDATE_DTO);
        assertEquals(reponseRuleDTO.getId(),RULE_ID);
        assertEquals(reponseRuleDTO.getName(),RULE_OTHER_NAME);
        assertEquals(reponseRuleDTO.getNumPenalties(),RULE_OTHER_NUM_PENALTIES);
        assertEquals(reponseRuleDTO.getDays(),RULE_OTHER_DAYS);
        assertEquals(reponseRuleDTO.getType(),RULE_TYPE);
        assertEquals(reponseRuleDTO.getAdminName(),ADMIN_FULL_NAME);
    }

    @Test
    void updateRuleWhenNotExistsRuleIdThrowNotFoundException(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            ruleService.update(RULE_ID,RULE_UPDATE_DTO);
        });
    }

    @Test
    void updateRuleWhenExistsOtherRuleWithSameDataThrowBadRequestException(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(true);
        when(this.ruleRepository.findById(RULE_ID)).thenReturn(Optional.of(RULE));
        when(this.ruleRepository.existsByNameAndNumPenaltiesAndDaysAndTypeAndIdNot(
                RULE_OTHER_NAME,RULE_OTHER_NUM_PENALTIES,RULE_OTHER_DAYS,RULE_TYPE,RULE_ID
        )).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            ruleService.update(RULE_ID,RULE_UPDATE_DTO);
        });
    }

    @Test
    void createRuleSuccessful(){
        when(this.adminService.getAdminByUserId(ADMIN_ID)).thenReturn(ADMIN);
        when(this.adminService.getUserFullNameByAdmin(any(Admin.class))).thenReturn(ADMIN_FULL_NAME);

        RuleAndRuleDTO reponseRuleAndRuleDTO  = this.ruleService.create(ADMIN_ID,RULE_CREATE_DTO,RULE_TYPE);
        RuleDTO ruleDTO = reponseRuleAndRuleDTO.getRuleDTO();

        assertEquals(ruleDTO.getName(),RULE_NAME);
        assertEquals(ruleDTO.getNumPenalties(),RULE_NUM_PENALTIES);
        assertEquals(ruleDTO.getDays(),RULE_DAYS);
        assertEquals(ruleDTO.getType(),RULE_TYPE);
        assertEquals(ruleDTO.getAdminName(),ADMIN_FULL_NAME);
    }

    @Test
    void createRuleWhenExistsOtherRuleWithSameDataThrowBadRequestException(){
        when(this.ruleRepository.existsByNameAndNumPenaltiesAndDaysAndType(
                RULE_NAME,RULE_NUM_PENALTIES,RULE_DAYS,RULE_TYPE
        )).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            ruleService.create(ADMIN_ID,RULE_CREATE_DTO,RULE_TYPE);
        });
    }

    @Test
    void deleteSuccessfulWhenExistsRule(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(true);
        when(this.ruleRepository.findById(RULE_ID)).thenReturn(Optional.of(RULE));
        ruleService.delete(RULE_ID);
    }
    @Test
    void deleteSuccessfulWhenNotExistsRule(){
        when(this.ruleRepository.existsById(RULE_ID)).thenReturn(false);
        ruleService.delete(RULE_ID);
    }
}
