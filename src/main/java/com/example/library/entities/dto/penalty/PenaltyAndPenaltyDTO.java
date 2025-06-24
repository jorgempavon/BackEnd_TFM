package com.example.library.entities.dto.penalty;

import com.example.library.entities.dto.penalty.PenaltyDTO;
import com.example.library.entities.model.penalty.Penalty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PenaltyAndPenaltyDTO {
    private Penalty penalty;
    private PenaltyDTO penaltyDTO;

    public PenaltyAndPenaltyDTO(){}

    public PenaltyAndPenaltyDTO(Penalty penalty, PenaltyDTO penaltyDTO){
        this.penalty = penalty;
        this.penaltyDTO = penaltyDTO;
    }
}
