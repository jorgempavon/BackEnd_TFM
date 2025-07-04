package com.example.library.api.resources.penalty;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyJustificationDTO;
import com.example.library.services.penalty.PenaltyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bibliokie/penalties")
@SecurityRequirement(name = "bearerAuth")
public class PenaltyResource {
    private final PenaltyService penaltyService;

    public PenaltyResource(PenaltyService penaltyService){
        this.penaltyService = penaltyService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public ResponseEntity<?> findByUserAndFulfilled(@RequestParam(required = true) Long userId,
                                                          @RequestParam(required = false) Boolean isFulfilled){

        List<PenaltyDTO> responseListRuleDTO = this.penaltyService.findByUserAndFulfilled(userId,isFulfilled);
        return ResponseEntity.ok(responseListRuleDTO);
    }
    @PutMapping("/{id}/forgive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forgivePenalty(@PathVariable(required = true) Long id,
                                            @Valid @RequestBody PenaltyJustificationDTO penaltyJustificationDTO){
        PenaltyDTO penaltyDTO = this.penaltyService.forgivePenalty(id,penaltyJustificationDTO);
        return ResponseEntity.ok(penaltyDTO);
    }

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<?> fulfillPenalty(@PathVariable(required = true) Long id,
                                            @Valid @RequestBody PenaltyJustificationDTO penaltyJustificationDTO,
                                            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getId();
        PenaltyDTO penaltyDTO = this.penaltyService.fulfillPenalty(id,penaltyJustificationDTO,userId);
        return ResponseEntity.ok(penaltyDTO);
    }
}
