package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.*;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.penalty.TemporaryPeriodPenalty;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.rule.TemporaryPeriodRule;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.penalty.BookingPeriodPenaltyRepository;
import com.example.library.entities.repository.penalty.TemporaryPeriodPenaltyRepository;
import com.example.library.services.EmailService;
import com.example.library.services.rule.BookingPeriodRuleService;
import com.example.library.services.rule.TemporaryPeriodRuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemporaryPeriodPenaltyServiceTest {
    @Mock
    private TemporaryPeriodPenaltyRepository temporaryPeriodPenaltyRepository;
    @Mock
    private PenaltyService penaltyService;
    @Mock
    private EmailService emailService;
    @Mock
    private TemporaryPeriodRuleService temporaryPeriodRuleService;
    @InjectMocks
    private TemporaryPeriodPenaltyService temporaryPeriodPenaltyService;

    private static final Long PENALTY_ID = 2L;
    private static final String TEMPORARY_PERIOD_RULE_NAME = "temporary period rule name";
    private static final String PENALTY_DESCRIPTION = "penalty description";
    private static final String PENALTY_JUSTIFICATION = "penalty justification";
    private static final String PENALTY_TYPE = "temporal";
    private static final String USER_NAME = "user name";
    private static final String CLIENT_FULL_NAME = "client full name";
    private static final String USER_LAST_NAME = "User last name";
    private static final String USER_DNI = "11112222Y";
    private static final String USER_EMAIL = "test@example.com";
    private static final Long USER_ID = 7L;
    private static final Date TEMPORARY_PERIOD_PENALTY_DATE = new Date();

    private static final User USER = new User(
            USER_ID,
            USER_NAME,
            USER_DNI,
            USER_EMAIL,
            USER_LAST_NAME
    );
    private static final Long CLIENT_ID = 5L;
    private static final Client CLIENT = new Client(
            CLIENT_ID,
            USER
    );
    private static final String BOOK_TITLE = "Titulo de libro";
    private static final Book BOOK = new Book("9781234567890", BOOK_TITLE,"Qr",new Date()
            ,5, "Fantasía", "Lorenzo");
    private static final BookingLoan BOOKING_LOAN = new BookingLoan(
            1L,new Date(),new Date(System.currentTimeMillis() + 86400000), false, false,BOOK,CLIENT
    );

    private static final Penalty PENALTY = new Penalty(
            PENALTY_ID,
            PENALTY_DESCRIPTION,
            PENALTY_JUSTIFICATION,
            false,
            false,
            PENALTY_TYPE,
            BOOKING_LOAN,
            CLIENT

    );
    private static final PenaltyCreateDTO PENALTY_CREATE_DTO = new PenaltyCreateDTO(
            PENALTY_DESCRIPTION,PENALTY_TYPE,BOOKING_LOAN,CLIENT
    );

    private static final PenaltyDTO PENALTY_DTO = new PenaltyDTO(PENALTY_ID,PENALTY_DESCRIPTION,PENALTY_TYPE,
                PENALTY_JUSTIFICATION,false,false,
            BOOK_TITLE,CLIENT_FULL_NAME
    );

    private static final PenaltyAndPenaltyDTO PENALTY_AND_PENALTY_DTO = new PenaltyAndPenaltyDTO(
            PENALTY,PENALTY_DTO,USER_EMAIL
    );
    private static final Long ADMIN_ID = 5L;
    private static final Admin ADMIN = new Admin(
            ADMIN_ID,
            USER
    );
    private static final Rule RULE = new Rule(2L,"rule name",8,10,ADMIN,"temporal");
    private static final TemporaryPeriodRule TEMPORARY_PERIOD_RULE = new TemporaryPeriodRule(
              3L,RULE
    );
    private static final TemporaryPeriodPenaltyCreateDTO TEMPORARY_PERIOD_PENALTY_CREATE_DTO = new TemporaryPeriodPenaltyCreateDTO(
            PENALTY_CREATE_DTO,TEMPORARY_PERIOD_PENALTY_DATE,TEMPORARY_PERIOD_RULE
    );
    private static final TemporaryPeriodPenalty TEMPORARY_PERIOD_PENALTY = new TemporaryPeriodPenalty(
            9L,new Date(),PENALTY,TEMPORARY_PERIOD_RULE
    );

    @Test
    void createTemporaryPeriodPenalty_Successful(){
        when(this.penaltyService.create(PENALTY_CREATE_DTO)).thenReturn(PENALTY_AND_PENALTY_DTO);
        when(this.temporaryPeriodRuleService.getRuleNameByTemporaryPeriodRule(any(TemporaryPeriodRule.class)))
                .thenReturn(TEMPORARY_PERIOD_RULE_NAME);

        TemporaryPeriodPenaltyDTO response = this.temporaryPeriodPenaltyService
                .create(TEMPORARY_PERIOD_PENALTY_CREATE_DTO);
        PenaltyDTO penaltyDTO = response.getPenaltyDTO();

        assertEquals(response.getTemporaryPeriodRuleName(),TEMPORARY_PERIOD_RULE_NAME);
        assertEquals(penaltyDTO.getId(),PENALTY_ID);
        assertEquals(penaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(penaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(penaltyDTO.getFulfilled(),false);
        assertEquals(penaltyDTO.getForgived(),false);

    }
    @Test
    void findTemporaryByPenaltyId_successful(){
        when(this.temporaryPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(true);
        when(this.temporaryPeriodPenaltyRepository.findByPenaltyId(PENALTY_ID)).thenReturn(Optional.of(TEMPORARY_PERIOD_PENALTY));
        when(this.penaltyService.findById(PENALTY_ID,USER_ID)).thenReturn(PENALTY_DTO);
        when(this.temporaryPeriodRuleService.getRuleNameByTemporaryPeriodRule(any(TemporaryPeriodRule.class)))
                .thenReturn(TEMPORARY_PERIOD_RULE_NAME);

        TemporaryPeriodPenaltyDTO response = this.temporaryPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID);
        PenaltyDTO penaltyDTO = response.getPenaltyDTO();

        assertEquals(response.getTemporaryPeriodRuleName(),TEMPORARY_PERIOD_RULE_NAME);
        assertEquals(penaltyDTO.getId(),PENALTY_ID);
        assertEquals(penaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(penaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(penaltyDTO.getFulfilled(),false);
        assertEquals(penaltyDTO.getForgived(),false);
    }

    @Test
    void findTemporaryByPenaltyId_whenNotExists_throwNotFoundException(){
        when(this.temporaryPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.temporaryPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID);
        });
    }
    @Test
    void delete_whenNotExistsTemporaryPenalty_successful(){
        when(this.temporaryPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(false);
        this.temporaryPeriodPenaltyService.deleteByPenaltyId(PENALTY_ID);
    }
    @Test
    void delete_whenExistsTemporaryPenalty_successful(){
        when(this.temporaryPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(true);
        when(this.temporaryPeriodPenaltyRepository.findByPenaltyId(PENALTY_ID)).thenReturn(Optional.of(TEMPORARY_PERIOD_PENALTY));
        this.temporaryPeriodPenaltyService.deleteByPenaltyId(PENALTY_ID);
    }
}
