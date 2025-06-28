package com.example.library.resources.penalty;

import com.example.library.api.resources.penalty.TemporaryPeriodPenaltyResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyDTO;
import com.example.library.services.penalty.TemporaryPeriodPenaltyService;
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
public class TemporaryPeriodPenaltyResourceTest {
    @Mock
    private TemporaryPeriodPenaltyService temporaryPeriodPenaltyService;
    @Mock
    private CustomUserDetails mockUserDetails;

    @InjectMocks
    private TemporaryPeriodPenaltyResource temporaryPeriodPenaltyResource;
    private static final Long USER_ID = 7L;
    private static final Long PENALTY_ID = 2L;


    @Test
    void deleteTemporaryPeriodPenalty_successful()
    {
        doNothing().when(this.temporaryPeriodPenaltyService).deleteByPenaltyId(PENALTY_ID);
        ResponseEntity<?> result = temporaryPeriodPenaltyResource.delete(PENALTY_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void findByIdTemporaryPeriodPenalty_successful()
    {
        PenaltyDTO penaltyDTO = new PenaltyDTO(PENALTY_ID,"PENALTY_DESCRIPTION","temporal",
                "penalty justification",false,false,
                "BOOK_TITLE","CLIENT_FULL_NAME",new Date()
        );
        TemporaryPeriodPenaltyDTO temporaryPeriodPenaltyDTO = new TemporaryPeriodPenaltyDTO(
                penaltyDTO,new Date(),"TEMPORARY PERIOD RULE NAME"
        );

        when(this.mockUserDetails.getId()).thenReturn(USER_ID);
        when(this.temporaryPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID)).thenReturn(temporaryPeriodPenaltyDTO);
        ResponseEntity<?> result = temporaryPeriodPenaltyResource.findById(this.mockUserDetails,PENALTY_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof TemporaryPeriodPenaltyDTO);
        TemporaryPeriodPenaltyDTO resultDTO  = (TemporaryPeriodPenaltyDTO) result.getBody();
        assertEquals(resultDTO,temporaryPeriodPenaltyDTO);
    }
}
