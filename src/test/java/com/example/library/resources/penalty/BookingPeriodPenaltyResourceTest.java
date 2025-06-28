package com.example.library.resources.penalty;

import com.example.library.api.resources.penalty.BookingPeriodPenaltyResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.services.penalty.BookingPeriodPenaltyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingPeriodPenaltyResourceTest {
    @Mock
    private BookingPeriodPenaltyService bookingPeriodPenaltyService;
    @Mock
    private CustomUserDetails mockUserDetails;

    @InjectMocks
    private BookingPeriodPenaltyResource bookingPeriodPenaltyResource;
    private static final Long USER_ID = 7L;
    private static final Long PENALTY_ID = 2L;


    @Test
    void deleteBookingPeriodPenalty_successful()
    {
        doNothing().when(this.bookingPeriodPenaltyService).deleteByPenaltyId(PENALTY_ID);
        ResponseEntity<?> result = bookingPeriodPenaltyResource.delete(PENALTY_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void findByIdBookingPeriodPenalty_successful()
    {
        PenaltyDTO penaltyDTO = new PenaltyDTO(PENALTY_ID,"PENALTY_DESCRIPTION","Intervalo de reserva",
                "penalty justification",false,false,
                "BOOK_TITLE","CLIENT_FULL_NAME",new Date()
        );
        BookingPeriodPenaltyDTO bookingPeriodPenaltyDTO= new BookingPeriodPenaltyDTO(
                penaltyDTO,20,"BOOKING PERIOD RULE NAME"
        );

        when(this.mockUserDetails.getId()).thenReturn(USER_ID);
        when(this.bookingPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID)).thenReturn(bookingPeriodPenaltyDTO);
        ResponseEntity<?> result = bookingPeriodPenaltyResource.findById(this.mockUserDetails,PENALTY_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof BookingPeriodPenaltyDTO);
        BookingPeriodPenaltyDTO resultDTO  = (BookingPeriodPenaltyDTO) result.getBody();
        assertEquals(resultDTO,bookingPeriodPenaltyDTO);
    }
}
