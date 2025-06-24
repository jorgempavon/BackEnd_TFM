package com.example.library.api.resources.penalty;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyCreateDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.services.penalty.TemporaryPeriodPenaltyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bibliokie/penalties")
@SecurityRequirement(name = "bearerAuth")
public class TemporaryPeriodPenaltyResource {
    private TemporaryPeriodPenaltyService temporaryPeriodPenaltyService;

    public TemporaryPeriodPenaltyResource(TemporaryPeriodPenaltyService temporaryPeriodPenaltyService){
        this.temporaryPeriodPenaltyService = temporaryPeriodPenaltyService;
    }
    /*
    @PostMapping
    public ResponseEntity<?> create( @Valid @RequestBody TemporaryPeriodPenaltyCreateDTO temporaryPeriodPenaltyCreateDTO) {
        TemporaryPeriodPenaltyDTO responsePenaltyCreateDTO = this.bookingPeriodRuleService.create(temporaryPeriodPenaltyCreateDTO);
        URI location = URI.create("/penalties/bookingPeriodRule/" + responseRuleCreateDTO.getId());
        return ResponseEntity.created(location).body(responsePenaltyCreateDTO);
    }
    */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.temporaryPeriodPenaltyService.deleteByPenaltyId(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ADMIN') ")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        TemporaryPeriodPenaltyDTO responseTemporaryPenaltyDTO = this.temporaryPeriodPenaltyService.findByPenaltyId(id);
        return ResponseEntity.ok(responseTemporaryPenaltyDTO);
    }
}
