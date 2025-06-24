package com.example.library.entities.dto.rule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleCreateDTO {
    @NotBlank(message = "El nombre de la regla es obligatorio")
    private String name;
    @NotNull(message = "El número de penalizaciones es obligatorio")
    private Integer numPenalties;
    @NotNull(message = "El número de días es obligatorio")
    private Integer days;

    public RuleCreateDTO(String name,Integer numPenalties, Integer days){
        this.name = name;
        this.numPenalties = numPenalties;
        this.days = days;
    }
}
