package com.example.library.entities.dto.penalty;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TemporaryPeriodPenaltyExistenceDTO {
    private Boolean existsPenalty;
    private Date endDate;

    public TemporaryPeriodPenaltyExistenceDTO(){}

    public TemporaryPeriodPenaltyExistenceDTO(Boolean existsPenalty,Date endDate){
        this.existsPenalty = existsPenalty;
        this.endDate = endDate;
    }
}
