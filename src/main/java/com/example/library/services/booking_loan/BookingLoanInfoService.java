package com.example.library.services.booking_loan;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.repository.BookingLoanRepository;
import com.example.library.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookingLoanInfoService {
    private final BookingLoanRepository bookingLoanRepository;
    private final BookService bookService;
    public BookingLoanInfoService(BookingLoanRepository bookingLoanRepository, BookService bookService){
        this.bookingLoanRepository = bookingLoanRepository;
        this.bookService = bookService;
    }
    public String getBookTitleByBookingLoan(BookingLoan bookingLoan){
        if(!this.bookingLoanRepository.existsById(bookingLoan.getId())){
            throw new NotFoundException("No existe la reserva con el id proporcionado");
        }
        return this.bookService.getBookTitleByBook(bookingLoan.getBook());
    }
}
