package com.example.library.resources;

import com.example.library.api.resources.BookingLoanResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanUpdateDTO;

import com.example.library.services.booking_loan.BookingLoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingLoanResourceTest {
    @Mock
    private BookingLoanService bookingLoanService;
    @Mock
    private CustomUserDetails userDetailsService;
    @InjectMocks
    private BookingLoanResource bookingLoanResource;

    private static final Date DATE = new Date();

    private static final Long BOOKING_LOAN_ID = 23L;
    private static final Long USER_ID = 34L;

    private static final  String BOOK_TITLE = "Book Title";

    private static final  String CLIENT_NAME = "Client Name";
    private static final BookingLoanDTO BOOKING_LOAN_DTO = new BookingLoanDTO(
            BOOKING_LOAN_ID,DATE,DATE,false,false,BOOK_TITLE,CLIENT_NAME
    );


    @Test
    void findBookingLoanByIdSuccessful(){
        when(this.bookingLoanService.findById(BOOKING_LOAN_ID,USER_ID)).thenReturn(BOOKING_LOAN_DTO);
        when(this.userDetailsService.getId()).thenReturn(USER_ID);
        ResponseEntity<?> result = bookingLoanResource.findById(userDetailsService,BOOKING_LOAN_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof BookingLoanDTO);
    }
    @Test
    void deleteBookingLoanByIdSuccessful(){
        when(this.userDetailsService.getId()).thenReturn(USER_ID);
        ResponseEntity<?> result = bookingLoanResource.delete(userDetailsService,BOOKING_LOAN_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateBookingLoanSuccessful(){
        BookingLoanUpdateDTO bookingLoanUpdateDTO = new BookingLoanUpdateDTO(
            DATE,DATE,false,false
        );
        when(this.bookingLoanService.update(BOOKING_LOAN_ID,bookingLoanUpdateDTO)).thenReturn(BOOKING_LOAN_DTO);

        ResponseEntity<?> result = bookingLoanResource.update(BOOKING_LOAN_ID,bookingLoanUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof BookingLoanDTO);
    }

    @Test
    void createBookingLoanSuccessful(){
        BookingLoanCreateDTO bookingLoanUpdateDTO = new BookingLoanCreateDTO(
                DATE,83L,17L
        );
        when(this.bookingLoanService.create(bookingLoanUpdateDTO)).thenReturn(BOOKING_LOAN_DTO);

        ResponseEntity<?> result = bookingLoanResource.create(bookingLoanUpdateDTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof BookingLoanDTO);
    }

    @Test
    void findBookingLoanByUserId(){
        List<BookingLoanDTO> LIST_BOOKING_LOAN_DTO = List.of(BOOKING_LOAN_DTO);
        when(this.bookingLoanService.findByUserId(USER_ID)).thenReturn(LIST_BOOKING_LOAN_DTO);

        ResponseEntity<?> result = bookingLoanResource.findByUserId(USER_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),LIST_BOOKING_LOAN_DTO);
    }
}
