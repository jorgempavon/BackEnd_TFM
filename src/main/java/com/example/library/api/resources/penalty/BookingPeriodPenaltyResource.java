package com.example.library.api.resources.penalty;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyDTO;
import com.example.library.services.penalty.BookingPeriodPenaltyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bibliokie/penalties/bookingPeriodPenalties")
@SecurityRequirement(name = "bearerAuth")
public class BookingPeriodPenaltyResource {
    private final BookingPeriodPenaltyService bookingPeriodPenaltyService;

    public BookingPeriodPenaltyResource(BookingPeriodPenaltyService bookingPeriodPenaltyService){
        this.bookingPeriodPenaltyService = bookingPeriodPenaltyService;
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
        this.bookingPeriodPenaltyService.deleteByPenaltyId(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id){
        Long userId = userDetails.getId();
        BookingPeriodPenaltyDTO responseBookingPeriodPenaltyDTO = this.bookingPeriodPenaltyService.findByPenaltyId(id,userId);
        return ResponseEntity.ok(responseBookingPeriodPenaltyDTO);
    }
}
