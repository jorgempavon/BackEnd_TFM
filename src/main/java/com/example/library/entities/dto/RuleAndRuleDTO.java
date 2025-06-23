package com.example.library.entities.dto;

import com.example.library.entities.model.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleAndRuleDTO {
    RuleDTO ruleDTO;
    Rule rule;

    public RuleAndRuleDTO(){

    }

    public RuleAndRuleDTO(RuleDTO ruleDTO, Rule rule){
        this.ruleDTO = ruleDTO;
        this.rule = rule;
    }
}
