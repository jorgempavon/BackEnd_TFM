package com.example.library.entities.dto.penalty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PenaltyJustificationDTO {
    @NotBlank(message = "La justificación de la penalización no puede ser nula")
    private String justificationPenalty;
    public PenaltyJustificationDTO(){}
    public PenaltyJustificationDTO(String justificationPenalty){
        this.justificationPenalty = justificationPenalty;
    }
}
