package com.example.library.entities.dto.rule;

import com.example.library.entities.model.rule.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleAndRuleDTO {
    RuleDTO ruleDTO;
    Rule rule;

    public RuleAndRuleDTO(){

    }

    public RuleAndRuleDTO( Rule rule,RuleDTO ruleDTO){
        this.ruleDTO = ruleDTO;
        this.rule = rule;
    }
}
