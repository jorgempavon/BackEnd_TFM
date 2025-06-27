package com.example.library.entities.dto.bookingLoan;

import com.example.library.entities.dto.validator.AtLeastOneField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AtLeastOneField(fields = {"beginDate","endDate","collected","returned"})
public class BookingLoanUpdateDTO {
    private Date beginDate;
    private Date endDate;
    private Boolean collected;
    private Boolean returned;
}
