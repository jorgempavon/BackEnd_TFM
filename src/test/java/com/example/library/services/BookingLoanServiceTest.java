package com.example.library.services;


import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.BookingPenaltyLookUpService;
import com.example.library.entities.TemporaryPenaltyLookUpService;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanUpdateDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyExistenceDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyExistenceDTO;
import com.example.library.entities.dto.rule.RuleDTO;
import com.example.library.entities.dto.rule.RuleExistenceDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.BookingLoanRepository;
import com.example.library.services.penalty.PenaltyService;
import com.example.library.services.rule.BookingPeriodRuleInfoService;
import com.example.library.services.rule.TemporaryPeriodRuleInfoService;
import com.example.library.services.user.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingLoanServiceTest {

    @Mock
    private BookingLoanRepository bookingLoanRepository;
    @Mock
    private BookService bookService;
    @Mock
    private ClientService clientService;
    @Mock
    private EmailService emailService;
    @Mock
    private PenaltyService penaltyService;
    @Mock
    private BookingPeriodRuleInfoService bookingPeriodRuleInfoService;
    @Mock
    private TemporaryPeriodRuleInfoService temporaryPeriodRuleInfoService;
    @Mock
    private TemporaryPenaltyLookUpService temporaryPeriodPenaltyService;
    @Mock
    private BookingPenaltyLookUpService bookingPeriodPenaltyService;
    @InjectMocks
    private BookingLoanService bookingLoanService;
    private static final Long BOOK_ID = 4L;
    private static final String BOOK_QR= "d9b2d63d-a233-4123-847a-7ac09a557b05\n";
    private static final String BOOK_ISBN = "9781234567890";
    private static final String BOOK_TITLE= "El Misterio del Bosque";
    private static final Integer BOOK_STOCK = 20;
    private static final Date BOOK_RELEASE_DATE = new Date();
    private static final String BOOK_GENRE = "Ficción";
    private static final String BOOK_AUTHOR = "Laura Márquez";
    private static final Long RULE_ID = 2L;
    private static final String RULE_NAME = "rule name";
    private static final Integer RULE_NUM_PENALTIES = 8;
    private static final Integer RULE_DAYS = 10;
    private static final String RULE_TYPE = "temporal";
    private static final String ADMIN_FULL_NAME = "Nombre completo admin";
    private static final RuleDTO RULE_DTO = new RuleDTO(
            RULE_ID,
            RULE_NUM_PENALTIES,
            RULE_DAYS,
            RULE_NAME,
            ADMIN_FULL_NAME,
            RULE_TYPE

    );
    private static final Book BOOK = new Book(
            BOOK_ID,
            BOOK_ISBN,
            BOOK_TITLE,
            BOOK_QR,
            BOOK_RELEASE_DATE,
            BOOK_STOCK,
            BOOK_GENRE,
            BOOK_AUTHOR
    );
    private static final Long CLIENT_ID = 24L;
    private static final Long USER_ID =2L;
    private static final String USER_EMAIL ="test@example.com";
    private static final User USER = new User(
            USER_ID,
            "example",
            "12345678A",
            USER_EMAIL,
            "last name example"
    );

    private static final Client CLIENT = new Client(
            CLIENT_ID,USER
    );
    private static final Date DATE = new Date();
    private static final Long BOOKING_LOAN_ID = 23L;
    private static final BookingLoan BOOKING_LOAN = new BookingLoan(
            BOOKING_LOAN_ID,DATE,DATE,false,false,BOOK,CLIENT
    );
    private static String CLIENT_FULL_NAME = "example last name example";

    private static Integer NUM_PENALTIES = 6;

    @Test
    void delete_BookingLoan_successful_existBookingLoan(){
        Calendar calAfter = Calendar.getInstance();
        calAfter.setTime(new Date());
        calAfter.add(Calendar.DAY_OF_MONTH, 4);
        Date dateAfter = calAfter.getTime();

        BookingLoan bookingLoan = new BookingLoan(
                BOOKING_LOAN_ID,dateAfter,dateAfter,false,false,BOOK,CLIENT
        );

        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookingLoanRepository.findById(BOOKING_LOAN_ID)).thenReturn(Optional.of(bookingLoan));
        when(this.clientService.isClientByUserId(USER_ID)).thenReturn(true);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(true);

        this.bookingLoanService.delete(BOOKING_LOAN_ID,USER_ID);
    }

    @Test
    void deleteBookingLoan_successful_notExistsBookingLoan(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(false);
        this.bookingLoanService.delete(BOOKING_LOAN_ID,USER_ID);
    }

    @Test
    void validateUserIsAdminOrOwnerOfBooking_throwsForbiddenException(){
        when(this.clientService.isClientByUserId(USER_ID)).thenReturn(true);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> {
            this.bookingLoanService.validateUserIsAdminOrOwnerOfBooking(CLIENT,USER_ID);
        });
    }
    @Test
    void deleteBookingLoan_successful_throwsConflictException(){
        Calendar calBefore = Calendar.getInstance();
        calBefore.setTime(new Date());
        calBefore.add(Calendar.DAY_OF_MONTH, -2);
        Date dateBefore = calBefore.getTime();

        BookingLoan bookingLoan = new BookingLoan(
                BOOKING_LOAN_ID,dateBefore,dateBefore,false,false,BOOK,CLIENT
        );

        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookingLoanRepository.findById(BOOKING_LOAN_ID)).thenReturn(Optional.of(bookingLoan));
        when(this.clientService.isClientByUserId(USER_ID)).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            this.bookingLoanService.delete(BOOKING_LOAN_ID,USER_ID);
        });
    }

    @Test
    void getBookTitleByBookingLoan_successful(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        String response = this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN);
        assertEquals(BOOK_TITLE,response);
    }

    @Test
    void getBookTitleByBookingLoan_notExists_throwNotFoundException(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        String response = this.bookingLoanService.getBookTitleByBookingLoan(BOOKING_LOAN);
        assertEquals(BOOK_TITLE,response);
    }

    @Test
    void validateBookingLoanCreationDate_throwsConflictException(){
        TemporaryPeriodPenaltyExistenceDTO temporaryExistenceDTO = new TemporaryPeriodPenaltyExistenceDTO(
                true,new Date()
        );
        Calendar calAfter = Calendar.getInstance();
        calAfter.setTime(new Date());
        calAfter.add(Calendar.DAY_OF_MONTH, 4);
        Date dateAfter = calAfter.getTime();

        when(this.temporaryPeriodPenaltyService.getTemporaryPeriodPenaltyByClientId(CLIENT_ID))
                .thenReturn(temporaryExistenceDTO);

        assertThrows(ConflictException.class, () -> {
            this.bookingLoanService.validateBookingLoanCreationDate(BOOK,CLIENT,dateAfter);
        });
    }
    @Test
    void validateBookingLoanCreationDate_throwsBadRequestException(){
        TemporaryPeriodPenaltyExistenceDTO temporaryExistenceDTO = new TemporaryPeriodPenaltyExistenceDTO(
                false,null
        );
        Book book = new Book(BOOK_ID,BOOK_ISBN,BOOK_TITLE,BOOK_QR,BOOK_RELEASE_DATE,0,BOOK_GENRE,BOOK_AUTHOR);

        Calendar calAfter = Calendar.getInstance();
        calAfter.setTime(new Date());
        calAfter.add(Calendar.DAY_OF_MONTH, 4);
        Date dateAfter = calAfter.getTime();

        when(this.temporaryPeriodPenaltyService.getTemporaryPeriodPenaltyByClientId(CLIENT_ID))
                .thenReturn(temporaryExistenceDTO);

        assertThrows(BadRequestException.class, () -> {
            this.bookingLoanService.validateBookingLoanCreationDate(book,CLIENT,dateAfter);
        });
    }

    @Test
    void validateBookingLoanCreationDate_BeginDateBefore_throwsConflictRequestException(){
        TemporaryPeriodPenaltyExistenceDTO temporaryExistenceDTO = new TemporaryPeriodPenaltyExistenceDTO(
                false,null
        );
        Calendar calBefore = Calendar.getInstance();
        calBefore.setTime(new Date());
        calBefore.add(Calendar.DAY_OF_MONTH, -2);
        Date dateBefore = calBefore.getTime();

        when(this.temporaryPeriodPenaltyService.getTemporaryPeriodPenaltyByClientId(CLIENT_ID))
                .thenReturn(temporaryExistenceDTO);

        assertThrows(ConflictException.class, () -> {
            this.bookingLoanService.validateBookingLoanCreationDate(BOOK,CLIENT,dateBefore);
        });
    }

    @Test
    void findByIdBookingLoan_successful(){

        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookingLoanRepository.findById(BOOKING_LOAN_ID)).thenReturn(Optional.of(BOOKING_LOAN));
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.clientService.isClientByUserId(USER_ID)).thenReturn(true);
        when(this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID)).thenReturn(true);

        BookingLoanDTO response = this.bookingLoanService.findById(BOOKING_LOAN_ID,USER_ID);

        assertEquals(response.getBeginDate(),DATE);
        assertEquals(response.getEndDate(),DATE);
        assertEquals(response.getCollected(),false);
        assertEquals(response.getReturned(),false);
        assertEquals(response.getClientName(),CLIENT_FULL_NAME);
        assertEquals(response.getBookTitle(),BOOK_TITLE);
    }

    @Test
    void findById_whenNotExistsBooking_throwNotFoundException(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.bookingLoanService.findById(BOOKING_LOAN_ID,USER_ID);
        });
    }

    @Test
    void createBookingLoan_successful(){
        Calendar calAfter = Calendar.getInstance();
        calAfter.setTime(new Date());
        calAfter.add(Calendar.DAY_OF_MONTH, 4);
        Date dateAfter = calAfter.getTime();
        
        BookingLoanCreateDTO bookingLoanCreateDTO = new BookingLoanCreateDTO(
                dateAfter,BOOK_ID,USER_ID
        );
        
        when(this.bookService.getBookByBookId(BOOK_ID)).thenReturn(BOOK);
        when(this.clientService.getClientByUserId(USER_ID)).thenReturn(CLIENT);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.clientService.getUserEmailByClient(CLIENT)).thenReturn(USER_EMAIL);

        TemporaryPeriodPenaltyExistenceDTO temporaryExistenceDTO = new TemporaryPeriodPenaltyExistenceDTO(
                false,null
        );
        when(this.temporaryPeriodPenaltyService.getTemporaryPeriodPenaltyByClientId(CLIENT_ID))
                .thenReturn(temporaryExistenceDTO);


        BookingPeriodPenaltyExistenceDTO bookingPeriodPenaltyExistenceDTO = new BookingPeriodPenaltyExistenceDTO(
                false,16
        );
        when(this.bookingPeriodPenaltyService.getBookingPeriodPenaltyByClientId((CLIENT_ID)))
                .thenReturn(bookingPeriodPenaltyExistenceDTO);

        BookingLoanDTO response = this.bookingLoanService.create(bookingLoanCreateDTO);
        assertEquals(response.getCollected(),false);
        assertEquals(response.getReturned(),false);
        assertEquals(response.getClientName(),CLIENT_FULL_NAME);
        assertEquals(response.getBookTitle(),BOOK_TITLE);
    }

    @Test
    void checkTemporaryPeriodPenalties_successful(){
        RuleExistenceDTO ruleExistenceDTO = new RuleExistenceDTO(true,RULE_DTO);
        when(this.temporaryPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(ruleExistenceDTO);

        this.bookingLoanService.checkTemporaryPeriodPenalties(NUM_PENALTIES,BOOKING_LOAN);
    }

    @Test
    void checkBookingPeriodPenalties_successful(){
        RuleExistenceDTO ruleExistenceDTO = new RuleExistenceDTO(true,RULE_DTO);
        when(this.bookingPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(ruleExistenceDTO);

        this.bookingLoanService.checkBookingPeriodPenalties(NUM_PENALTIES,BOOKING_LOAN);
    }

    @Test
    void checkReturnedBookingHasPenalties_successful(){
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(new Date());
        calBegin.add(Calendar.DAY_OF_MONTH, -19);
        Date beginDate = calBegin.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -4);
        Date endDate = cal.getTime();

        BookingLoan bookingLoan = new BookingLoan(
                beginDate,endDate,true,false,BOOK,CLIENT
        );

        RuleExistenceDTO ruleExistenceDTO = new RuleExistenceDTO(false,null);
        when(this.temporaryPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(ruleExistenceDTO);
        when(this.bookingPeriodRuleInfoService.findByNumPenalties(NUM_PENALTIES)).thenReturn(ruleExistenceDTO);
        when(this.penaltyService.getNumPenaltiesOfClient(CLIENT)).thenReturn(NUM_PENALTIES);
        this.bookingLoanService.checkReturnedBookingHasPenalties(bookingLoan);
    }

    @Test
    void updateBookingLoan_whenNotExistsId_throwNotFoundException(){
        BookingLoanUpdateDTO bookingLoanUpdateDTO = new BookingLoanUpdateDTO(
            new Date(),new Date(),true,true
        );
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.bookingLoanService.update(BOOKING_LOAN_ID,bookingLoanUpdateDTO);
        });
    }

    @Test
    void updateBookingLoan_successful(){
        Calendar calBeginUpdate = Calendar.getInstance();
        calBeginUpdate.setTime(new Date());
        calBeginUpdate.add(Calendar.DAY_OF_MONTH, -13);
        Date beginDateUpdate = calBeginUpdate.getTime();

        Calendar calUpdate = Calendar.getInstance();
        calUpdate.setTime(new Date());
        calUpdate.add(Calendar.DAY_OF_MONTH, 8);
        Date endDateUpdate = calUpdate.getTime();

        BookingLoanUpdateDTO bookingLoanUpdateDTO = new BookingLoanUpdateDTO(
                beginDateUpdate,endDateUpdate,true,true
        );
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(new Date());
        calBegin.add(Calendar.DAY_OF_MONTH, -19);
        Date beginDate = calBegin.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date endDate = cal.getTime();
        BookingLoan bookingLoan = new BookingLoan(
            BOOKING_LOAN_ID, beginDate, endDate, false,false,BOOK,CLIENT
        );
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookingLoanRepository.findById(BOOKING_LOAN_ID)).thenReturn(Optional.of(bookingLoan));
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        BookingLoanDTO response = this.bookingLoanService.update(BOOKING_LOAN_ID,bookingLoanUpdateDTO);

        assertEquals(response.getCollected(),true);
        assertEquals(response.getReturned(),true);
        assertEquals(response.getClientName(),CLIENT_FULL_NAME);
        assertEquals(response.getBookTitle(),BOOK_TITLE);
    }


    @Test
    void findByUserId_successful(){
        List<BookingLoan> mockBookingLoans = List.of(BOOKING_LOAN);

        when(this.clientService.getClientIdByUserId(USER_ID)).thenReturn(CLIENT_ID);
        when(this.bookingLoanRepository.existsByClientId(CLIENT_ID)).thenReturn(true);
        when(bookingLoanRepository.findByClientId(CLIENT_ID)).thenReturn(Optional.of(mockBookingLoans));
        when(this.clientService.getUserFullNameByClient(CLIENT)).thenReturn(CLIENT_FULL_NAME);
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        List<BookingLoanDTO> result = bookingLoanService.findByUserId(USER_ID);

        assertEquals(result.get(0).getCollected(),false);
        assertEquals(result.get(0).getReturned(),false);
        assertEquals(result.get(0).getClientName(),CLIENT_FULL_NAME);
        assertEquals(result.get(0).getBookTitle(),BOOK_TITLE);
    }

    @Test
    void findByUserId_withNoBookings(){
        when(this.clientService.getClientIdByUserId(USER_ID)).thenReturn(CLIENT_ID);
        when(this.bookingLoanRepository.existsByClientId(CLIENT_ID)).thenReturn(false);
        List<BookingLoanDTO> result = bookingLoanService.findByUserId(USER_ID);

        assertEquals(0, result.size());
    }


}
