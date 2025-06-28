package com.example.library.entities.dto.bookingLoan;

import com.example.library.entities.model.Book;
import com.example.library.entities.model.user.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookingLoanCreateDTO {
    @NotNull
    private Date beginDate;
    @NotNull
    private Long bookId;
    @NotNull
    private Long userId;

    public BookingLoanCreateDTO(){

    }

    public BookingLoanCreateDTO(Date beginDate,Long bookId,Long userId){
        this.beginDate = beginDate;
        this.bookId = bookId;
        this.userId = userId;
    }

}
