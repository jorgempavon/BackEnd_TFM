package com.example.library.entities.model.rule;

import com.example.library.entities.model.user.Admin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "rule", schema = "library")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "numpenalties", nullable = false)
    private Integer numPenalties;
    @Column(name = "days", nullable = false)
    private Integer days;
    @Column(name = "type",nullable = false)
    private String type;
    @OneToOne
    @JoinColumn(name = "adminid", referencedColumnName = "id", unique = true)
    private Admin admin;

    public Rule(){

    }

    public Rule(String name,Integer numPenalties,Integer days,Admin admin,String type){
        this.name = name;
        this.numPenalties = numPenalties;
        this.admin = admin;
        this.days = days;
        this.type = type;
    }

    public Rule(Long id, String name,Integer numPenalties,Integer days,Admin admin,String type){
        this.id = id;
        this.name = name;
        this.numPenalties = numPenalties;
        this.admin = admin;
        this.type = type;
        this.days = days;
    }


}
