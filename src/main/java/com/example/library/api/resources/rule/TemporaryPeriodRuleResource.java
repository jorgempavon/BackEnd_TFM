package com.example.library.api.resources.rule;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.services.rule.TemporaryPeriodRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bibliokie/rules/temporaryPeriodRule")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class TemporaryPeriodRuleResource {
    private final TemporaryPeriodRuleService temporaryPeriodRuleService;

    public TemporaryPeriodRuleResource(TemporaryPeriodRuleService temporaryPeriodRuleService){
        this.temporaryPeriodRuleService = temporaryPeriodRuleService;
    }
    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody RuleCreateDTO ruleCreateDTO) {
        Long id = userDetails.getId();
        RuleDTO responseRuleCreateDTO = this.temporaryPeriodRuleService.create(id,ruleCreateDTO);
        URI location = URI.create("/rules/temporaryPeriodRule/" + responseRuleCreateDTO.getId());
        return ResponseEntity.created(location).body(responseRuleCreateDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.temporaryPeriodRuleService.deleteByRuleId(id);
        return ResponseEntity.ok().build();
    }
}
