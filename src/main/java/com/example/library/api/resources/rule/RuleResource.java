package com.example.library.api.resources.rule;

import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleUpdateDTO;
import com.example.library.services.rule.RuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bibliokie/rules")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class RuleResource {

    private final RuleService ruleService;

    public RuleResource(RuleService ruleService){
        this.ruleService = ruleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        RuleDTO responseRuleDTO= this.ruleService.findById(id);
        return ResponseEntity.ok(responseRuleDTO);
    }
    @GetMapping
    public ResponseEntity<?> findByNameAndNumMimPenalties(@RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) Integer minNumPenalties){

        List<RuleDTO> responseListRuleDTO = this.ruleService.findByNameAndNumMimPenalties(name,minNumPenalties);
        return ResponseEntity.ok(responseListRuleDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@Valid @RequestBody RuleUpdateDTO ruleUpdateDTO) {
        RuleDTO responseRuleDTO = this.ruleService.update(id,ruleUpdateDTO);
        return ResponseEntity.ok(responseRuleDTO);
    }
}
