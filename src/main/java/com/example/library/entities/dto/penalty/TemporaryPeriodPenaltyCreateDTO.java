package com.example.library.entities.dto.penalty;

import com.example.library.entities.model.rule.TemporaryPeriodRule;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TemporaryPeriodPenaltyCreateDTO {
    @NotNull(message = "Es obligatorio proporcionar los atributos de la penalización")
    private PenaltyCreateDTO penaltyCreateDTO;
    @NotNull(message = "Es obligatorio proporcionar la fecha de la penalización")
    private Date endDate;
    @NotNull(message = "Es obligatorio proporcionar la penalización temporal")
    private TemporaryPeriodRule temporaryPeriodRule;

    public TemporaryPeriodPenaltyCreateDTO(){

    }
    public TemporaryPeriodPenaltyCreateDTO(PenaltyCreateDTO penaltyCreateDTO,Date endDate,TemporaryPeriodRule temporaryPeriodRule){
        this.penaltyCreateDTO = penaltyCreateDTO;
        this.endDate = endDate;
        this.temporaryPeriodRule = temporaryPeriodRule;
    }
}
