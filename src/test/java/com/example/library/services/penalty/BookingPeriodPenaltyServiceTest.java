package com.example.library.services.penalty;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.penalty.*;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.penalty.BookingPeriodPenalty;
import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.rule.BookingPeriodRule;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.penalty.BookingPeriodPenaltyRepository;
import com.example.library.services.EmailService;
import com.example.library.services.rule.BookingPeriodRuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingPeriodPenaltyServiceTest {
    @Mock
    private BookingPeriodPenaltyRepository bookingPeriodPenaltyRepository;
    @Mock
    private PenaltyService penaltyService;
    @Mock
    private EmailService emailService;
    @Mock
    private BookingPeriodRuleService bookingPeriodRuleService;
    @InjectMocks
    private BookingPeriodPenaltyService bookingPeriodPenaltyService;

    private static final Long PENALTY_ID = 2L;
    private static final String BOOKING_PERIOD_RULE_NAME = "Booking period rule name";
    private static final String PENALTY_DESCRIPTION = "penalty description";
    private static final String PENALTY_JUSTIFICATION = "penalty justification";
    private static final String PENALTY_TYPE = "Intervalo de reserva";
    private static final String CLIENT_FULL_NAME = "client full name";
    private static final String USER_EMAIL = "test@example.com";
    private static final Long USER_ID = 7L;
    private static final Integer BOOKING_PERIOD_DAYS = 30;

    private static final User USER = new User(
            USER_ID,
            "user name",
            "11112222Y",
            USER_EMAIL,
            "User last name"
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
            BOOK_TITLE,CLIENT_FULL_NAME,new Date()
    );

    private static final PenaltyAndPenaltyDTO PENALTY_AND_PENALTY_DTO = new PenaltyAndPenaltyDTO(
            PENALTY,PENALTY_DTO,USER_EMAIL
    );
    private static final Long ADMIN_ID = 5L;
    private static final Admin ADMIN = new Admin(
            ADMIN_ID,
            USER
    );
    private static final Rule RULE = new Rule(2L,"rule name",8,10,ADMIN,PENALTY_TYPE);
    private static final BookingPeriodRule BOOKING_PERIOD_RULE = new BookingPeriodRule(
            3L,RULE
    );
    private static final BookingPeriodPenaltyCreateDTO BOOKING_PERIOD_PENALTY_CREATE_DTO = new BookingPeriodPenaltyCreateDTO(
            PENALTY_CREATE_DTO,BOOKING_PERIOD_DAYS,BOOKING_PERIOD_RULE
    );
    private static final BookingPeriodPenalty TEMPORARY_PERIOD_PENALTY = new BookingPeriodPenalty(
            9L,BOOKING_PERIOD_DAYS,PENALTY,BOOKING_PERIOD_RULE
    );

    @Test
    void createTemporaryPeriodPenaltySuccessful(){
        when(this.penaltyService.create(PENALTY_CREATE_DTO)).thenReturn(PENALTY_AND_PENALTY_DTO);
        when(this.bookingPeriodRuleService.getRuleNameByBookingPeriodRule(any(BookingPeriodRule.class)))
                .thenReturn(BOOKING_PERIOD_RULE_NAME);

        BookingPeriodPenaltyDTO response = this.bookingPeriodPenaltyService
                .create(BOOKING_PERIOD_PENALTY_CREATE_DTO);
        PenaltyDTO penaltyDTO = response.getPenaltyDTO();

        assertEquals(response.getBookingPeriodRuleName(),BOOKING_PERIOD_RULE_NAME);
        assertEquals(penaltyDTO.getId(),PENALTY_ID);
        assertEquals(penaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(penaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(penaltyDTO.getFulfilled(),false);
        assertEquals(penaltyDTO.getForgived(),false);

    }
    @Test
    void findTemporaryByPenaltyIdSuccessful(){
        when(this.bookingPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(true);
        when(this.bookingPeriodPenaltyRepository.findByPenaltyId(PENALTY_ID)).thenReturn(Optional.of(TEMPORARY_PERIOD_PENALTY));
        when(this.penaltyService.findById(PENALTY_ID,USER_ID)).thenReturn(PENALTY_DTO);
        when(this.bookingPeriodRuleService.getRuleNameByBookingPeriodRule(any(BookingPeriodRule.class)))
                .thenReturn(BOOKING_PERIOD_RULE_NAME);

        BookingPeriodPenaltyDTO response = this.bookingPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID);
        PenaltyDTO penaltyDTO = response.getPenaltyDTO();

        assertEquals(response.getBookingPeriodRuleName(),BOOKING_PERIOD_RULE_NAME);
        assertEquals(penaltyDTO.getId(),PENALTY_ID);
        assertEquals(penaltyDTO.getDescription(),PENALTY_DESCRIPTION);
        assertEquals(penaltyDTO.getType(),PENALTY_TYPE);
        assertEquals(penaltyDTO.getJustificationPenalty(),PENALTY_JUSTIFICATION);
        assertEquals(penaltyDTO.getFulfilled(),false);
        assertEquals(penaltyDTO.getForgived(),false);
    }

    @Test
    void findTemporaryByPenaltyIdWhenNotExistsThrowNotFoundException(){
        when(this.bookingPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () ->
            this.bookingPeriodPenaltyService.findByPenaltyId(PENALTY_ID,USER_ID));
    }
    @Test
    void deleteWhenNotExistsTemporaryPenaltySuccessful(){
        when(this.bookingPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(false);
        this.bookingPeriodPenaltyService.deleteByPenaltyId(PENALTY_ID);
    }
    @Test
    void deleteWhenExistsTemporaryPenaltySuccessful(){
        when(this.bookingPeriodPenaltyRepository.existsByPenaltyId(PENALTY_ID)).thenReturn(true);
        when(this.bookingPeriodPenaltyRepository.findByPenaltyId(PENALTY_ID)).thenReturn(Optional.of(TEMPORARY_PERIOD_PENALTY));
        this.bookingPeriodPenaltyService.deleteByPenaltyId(PENALTY_ID);
    }

    @Test
    void getBookingPeriodPenaltyByClientIdReturnsNotExists(){
        PenaltyExistenceDTO penaltyExistenceDTO = new PenaltyExistenceDTO(
            false,PENALTY_ID
        );
        when(this.penaltyService.getPenaltyByClientIdAndType(CLIENT_ID,PENALTY_TYPE)).thenReturn(penaltyExistenceDTO);

        BookingPeriodPenaltyExistenceDTO response = this.bookingPeriodPenaltyService.getBookingPeriodPenaltyByClientId(CLIENT_ID);

        assertFalse(response.getExistsPenalty());
    }

    @Test
    void getBookingPeriodPenaltyByClientIdReturnsExists(){
        PenaltyExistenceDTO penaltyExistenceDTO = new PenaltyExistenceDTO(
                true,PENALTY_ID
        );
        BookingPeriodPenalty bookingPeriodPenalty = new BookingPeriodPenalty(
                24L,BOOKING_PERIOD_DAYS,PENALTY,BOOKING_PERIOD_RULE
        );
        when(this.penaltyService.getPenaltyByClientIdAndType(CLIENT_ID,PENALTY_TYPE)).thenReturn(penaltyExistenceDTO);
        when(this.bookingPeriodPenaltyRepository.findByPenaltyId(PENALTY_ID)).thenReturn(Optional.of(bookingPeriodPenalty));
        BookingPeriodPenaltyExistenceDTO response = this.bookingPeriodPenaltyService.getBookingPeriodPenaltyByClientId(CLIENT_ID);

        assertTrue(response.getExistsPenalty());
        assertEquals(response.getDays(),BOOKING_PERIOD_DAYS);
    }

}
