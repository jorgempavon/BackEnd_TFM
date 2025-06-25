package com.example.library.resources.penalty;
import com.example.library.api.resources.penalty.PenaltyResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.PenaltyCreateDTO;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.PenaltyJustificationDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.services.penalty.PenaltyService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PenaltyResourceTest {
    @Mock
    private PenaltyService penaltyService;
    @Mock
    private CustomUserDetails mockUserDetails;
    @InjectMocks
    private PenaltyResource penaltyResource;

    private static final Long PENALTY_ID = 2L;
    private static final String PENALTY_DESCRIPTION = "penalty description";
    private static final String PENALTY_JUSTIFICATION = "penalty justification";
    private static final String PENALTY_TYPE = "temporal";
    private static final String CLIENT_FULL_NAME = "client full name";
    private static final Long USER_ID = 7L;

    private static final String BOOK_TITLE = "Titulo de libro";
    private static final PenaltyJustificationDTO PENALTY_JUSTIFICATION_DTO = new PenaltyJustificationDTO(
            PENALTY_JUSTIFICATION
    );
    private static final PenaltyDTO PENALTY_DTO = new PenaltyDTO(PENALTY_ID,PENALTY_DESCRIPTION,PENALTY_TYPE,
            PENALTY_JUSTIFICATION,false,false,
            BOOK_TITLE,CLIENT_FULL_NAME
    );

    @Test
    void findByUserAndFulfilled_successful(){
        List<PenaltyDTO> LIST_PENALTIES_DTO = List.of(PENALTY_DTO);
        when(this.penaltyService.findByUserAndFulfilled(USER_ID,false)).thenReturn(LIST_PENALTIES_DTO);

        ResponseEntity<?> result = penaltyResource.findByUserAndFulfilled(USER_ID,false);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    @Test
    void forgivePenalty_successful(){
        when(this.penaltyService.forgivePenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO)).thenReturn(PENALTY_DTO);

        ResponseEntity<?> result = penaltyResource.forgivePenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),PENALTY_DTO);
    }
    @Test
    void fulfillPenalty_successful(){
        when(this.mockUserDetails.getId()).thenReturn(USER_ID);
        when(this.penaltyService.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,USER_ID)).thenReturn(PENALTY_DTO);
        ResponseEntity<?> result = penaltyResource.fulfillPenalty(PENALTY_ID,PENALTY_JUSTIFICATION_DTO,this.mockUserDetails);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),PENALTY_DTO);
    }
}
