package com.example.library.services.rule;

import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.repository.rule.RuleRepository;
import com.example.library.services.user.AdminService;
import org.springframework.stereotype.Service;

@Service
public class RuleInfoService {
    private final RuleRepository ruleRepository;
    private final AdminService adminService;

    public RuleInfoService(RuleRepository ruleRepository,AdminService adminService){
        this.ruleRepository = ruleRepository;
        this.adminService = adminService;
    }

    public RuleExistenceDTO findByNumPenalties(Integer numPenalties){
        RuleExistenceDTO ruleExistenceDTO = new RuleExistenceDTO(false,null);
        if (!this.ruleRepository.existsByNumPenalties(numPenalties)){
            return ruleExistenceDTO;
        }
        Rule rule = this.ruleRepository.findByNumPenalties(numPenalties).get().get(0);

        String adminFullName = this.adminService.getUserFullNameByAdmin(rule.getAdmin());
        RuleDTO ruleDTO = new RuleDTO(rule.getId(), rule.getNumPenalties(),rule.getDays(),rule.getName()
                ,adminFullName,rule.getType());

        ruleExistenceDTO.setExistsRule(true);
        ruleExistenceDTO.setRuleDTO(ruleDTO);

        return ruleExistenceDTO;
    }
}
