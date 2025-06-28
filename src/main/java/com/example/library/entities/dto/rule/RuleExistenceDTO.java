package com.example.library.entities.dto.rule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleExistenceDTO {

    private Boolean existsRule;
    private RuleDTO ruleDTO;

    public RuleExistenceDTO(){}

    public RuleExistenceDTO(Boolean existsRule,RuleDTO ruleDTO){
        this.existsRule = existsRule;
        this.ruleDTO = ruleDTO;
    }

}
