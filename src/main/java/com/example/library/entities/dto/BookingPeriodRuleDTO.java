package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingPeriodRuleDTO {
    private String name;
    private Integer numPenalties;
    private Integer days;

    public BookingPeriodRuleDTO(String name,Integer numPenalties, Integer days){
        this.name = name;
        this.numPenalties = numPenalties;
        this.days = days;
    }
}
