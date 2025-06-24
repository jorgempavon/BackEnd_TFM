package com.example.library.services;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.repository.BookingLoanRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingLoanService {
    private BookingLoanRepository bookingLoanRepository;
    private BookService bookService;

    public BookingLoanService(BookingLoanRepository bookingLoanRepository,
                              BookService bookService){
        this.bookingLoanRepository = bookingLoanRepository;
        this.bookService = bookService;
    }

    public String getBookTitleByBookingLoan(BookingLoan bookingLoan){
        if(!this.bookingLoanRepository.existsById(bookingLoan.getId())){
            throw new NotFoundException("No existe la reserva con el id proporcionado")
        }
        return this.bookService.getBookTitleByBook(bookingLoan.getBook());
    }
}
