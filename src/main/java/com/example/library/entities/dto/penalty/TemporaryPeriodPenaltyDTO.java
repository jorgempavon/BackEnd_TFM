package com.example.library.entities.dto.penalty;

import com.example.library.entities.dto.penalty.PenaltyDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TemporaryPeriodPenaltyDTO {
    private PenaltyDTO penaltyDTO;
    private Date endDate;
    private String temporaryPeriodRuleName;

    public TemporaryPeriodPenaltyDTO(){

    }
    public TemporaryPeriodPenaltyDTO(PenaltyDTO penaltyDTO,Date endDate,String temporaryPeriodRuleName){
        this.penaltyDTO = penaltyDTO;
        this.endDate = endDate;
        this.temporaryPeriodRuleName = temporaryPeriodRuleName;
    }
}
