package com.example.library.services;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.user.UserAndUserDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.BookingLoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingLoanInfoServiceTest {
    @Mock
    private BookingLoanRepository bookingLoanRepository;
    @Mock
    private BookService bookService;

    @InjectMocks
    private BookingLoanInfoService bookingLoanInfoService;
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
    private static final User USER = new User(
            2L,
            "example",
            "12345678A",
            "test@example.com",
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

    @Test
    void getBookTitleByBookingLoan_successful(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        String response = this.bookingLoanInfoService.getBookTitleByBookingLoan(BOOKING_LOAN);
        assertEquals(BOOK_TITLE,response);
    }

    @Test
    void getBookTitleByBookingLoan_notExists_throwNotFoundException(){
        when(this.bookingLoanRepository.existsById(BOOKING_LOAN_ID)).thenReturn(true);
        when(this.bookService.getBookTitleByBook(BOOK)).thenReturn(BOOK_TITLE);

        String response = this.bookingLoanInfoService.getBookTitleByBookingLoan(BOOKING_LOAN);
        assertEquals(BOOK_TITLE,response);
    }

}
