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

    public RuleDTO(Integer numPenalties,Integer days,String adminName,String type){
        this.numPenalties = numPenalties;
        this.adminName = adminName;
        this.days = days;
        this.type = type;
    }

    public RuleDTO(Long id, Integer numPenalties, Integer days,String adminName,String type){
        this.id = id;
        this.numPenalties = numPenalties;
        this.adminName = adminName;
        this.type = type;
        this.days = days;
    }

}
