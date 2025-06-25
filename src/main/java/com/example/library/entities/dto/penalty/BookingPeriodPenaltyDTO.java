package com.example.library.entities.dto.penalty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingPeriodPenaltyDTO {
    private PenaltyDTO penaltyDTO;
    private Integer days;
    private String bookingPeriodRuleName;

    public BookingPeriodPenaltyDTO(){

    }
    public BookingPeriodPenaltyDTO(PenaltyDTO penaltyDTO,Integer days,String bookingPeriodRuleName){
        this.penaltyDTO = penaltyDTO;
        this.days = days;
        this.bookingPeriodRuleName = bookingPeriodRuleName;
    }
}
