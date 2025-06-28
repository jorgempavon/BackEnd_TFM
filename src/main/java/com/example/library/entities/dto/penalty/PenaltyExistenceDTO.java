package com.example.library.entities.dto.penalty;

import com.example.library.entities.model.penalty.Penalty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PenaltyExistenceDTO {
    private Boolean existsPenalty;
    private Long penaltyId;

    public PenaltyExistenceDTO(){}

    public PenaltyExistenceDTO(Boolean existsPenalty,Long penaltyId){
        this.existsPenalty = existsPenalty;
        this.penaltyId = penaltyId;
    }
}
