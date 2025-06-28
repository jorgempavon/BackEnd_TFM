package com.example.library.services;


import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.ForbiddenException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.BookingPenaltyLookUpService;
import com.example.library.entities.TemporaryPenaltyLookUpService;
import com.example.library.entities.dto.bookingLoan.BookingLoanCreateDTO;
import com.example.library.entities.dto.bookingLoan.BookingLoanDTO;
import com.example.library.entities.dto.penalty.BookingPeriodPenaltyExistenceDTO;
import com.example.library.entities.dto.penalty.TemporaryPeriodPenaltyExistenceDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.BookingLoanRepository;
import com.example.library.services.user.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
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

}
