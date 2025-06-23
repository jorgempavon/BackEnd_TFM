package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AtLeastOneField(fields = {"name","numPenalties","days"})
public class RuleUpdateDTO {

    @NotBlank(message = "El nombre de la regla es obligatorio")
    private String name;
    @NotNull(message = "El número de penalizaciones es obligatorio")
    private Integer numPenalties;
    @NotNull(message = "El número de días es obligatorio")
    private Integer days;

    public RuleUpdateDTO(String name,Integer numPenalties, Integer days){
        this.name = name;
        this.numPenalties = numPenalties;
        this.days = days;
    }
}
