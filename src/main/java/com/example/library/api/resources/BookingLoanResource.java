package com.example.library.api.resources;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanUpdateDTO;
import com.example.library.services.bookingLoan.BookingLoanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bibliokie/bookingLoans")
@SecurityRequirement(name = "bearerAuth")
public class BookingLoanResource {

    private final BookingLoanService bookingLoanService;

    public BookingLoanResource(BookingLoanService bookingLoanService){
        this.bookingLoanService = bookingLoanService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BookingLoanCreateDTO bookingLoanCreateDTO) {
        BookingLoanDTO responseBookDTO = this.bookingLoanService.create(bookingLoanCreateDTO);
        URI location = URI.create("/bookingLoans/" + responseBookDTO.getId());
        return ResponseEntity.created(location).body(responseBookDTO);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> findById(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id){
        Long userId = userDetails.getId();
        BookingLoanDTO responseBookDTO= this.bookingLoanService.findById(id,userId);
        return ResponseEntity.ok(responseBookDTO);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> delete(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable Long id){
        Long userId = userDetails.getId();
        this.bookingLoanService.delete(id,userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public  ResponseEntity<?> findByUserId(@RequestParam(required = true) Long userId){
        List<BookingLoanDTO> responseListBookDTO = this.bookingLoanService.findByUserId(userId);
        return ResponseEntity.ok(responseListBookDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody BookingLoanUpdateDTO bookingLoanUpdateDTO) {
        BookingLoanDTO responseBookDTO = this.bookingLoanService.update(id,bookingLoanUpdateDTO);
        return ResponseEntity.ok(responseBookDTO);
    }
}
