package com.example.library.entities.dto.penalty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingPeriodPenaltyExistenceDTO {
    private Boolean existsPenalty;
    private Integer days;

    public BookingPeriodPenaltyExistenceDTO(){}

    public BookingPeriodPenaltyExistenceDTO(Boolean existsPenalty,Integer days){
        this.existsPenalty = existsPenalty;
        this.days = days;
    }

}
