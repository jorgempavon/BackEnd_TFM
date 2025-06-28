package com.example.library.entities.dto.bookingLoan;

import com.example.library.entities.model.Book;
import com.example.library.entities.model.user.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingLoanDTO {
    private Long id;
    private Date beginDate;
    private Date endDate;
    private Boolean collected;
    private Boolean returned;
    private String bookTitle;
    private String clientName;

    public BookingLoanDTO(){

    }

    public BookingLoanDTO(Date beginDate,Date endDate,Boolean collected, Boolean returned, String bookTitle, String clientName){
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.collected = collected;
        this.returned = returned;
        this.bookTitle = bookTitle;
        this.clientName = clientName;
    }

    public BookingLoanDTO(Long id,Date beginDate,Date endDate,Boolean collected, Boolean returned,String bookTitle, String clientName){
        this.id = id;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.collected = collected;
        this.returned = returned;
        this.bookTitle = bookTitle;
        this.clientName = clientName;
    }
}
