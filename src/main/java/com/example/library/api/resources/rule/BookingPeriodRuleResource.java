package com.example.library.api.resources.rule;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.rule.RuleCreateDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.services.rule.BookingPeriodRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bibliokie/rules/bookingPeriodRule")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class BookingPeriodRuleResource {
    private final BookingPeriodRuleService bookingPeriodRuleService;

    public BookingPeriodRuleResource(BookingPeriodRuleService bookingPeriodRuleService){
        this.bookingPeriodRuleService = bookingPeriodRuleService;
    }
    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody RuleCreateDTO ruleCreateDTO) {
        Long id = userDetails.getId();
        RuleDTO responseRuleCreateDTO = this.bookingPeriodRuleService.create(id,ruleCreateDTO);
        URI location = URI.create("/rules/bookingPeriodRule/" + responseRuleCreateDTO.getId());
        return ResponseEntity.created(location).body(responseRuleCreateDTO);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.bookingPeriodRuleService.deleteByRuleId(id);
        return ResponseEntity.ok().build();
    }
}
