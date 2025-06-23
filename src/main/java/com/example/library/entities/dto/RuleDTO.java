package com.example.library.entities.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleDTO {

    private Long id;
    private String name;
    private Integer numPenalties;
    private Integer days;
    private String type;
    private String adminName;

    public RuleDTO(){

    }


    public RuleDTO(Long id, Integer numPenalties, Integer days,String name,String adminName,String type){
        this.id = id;
        this.name = name;
        this.numPenalties = numPenalties;
        this.adminName = adminName;
        this.type = type;
        this.days = days;
    }

}
