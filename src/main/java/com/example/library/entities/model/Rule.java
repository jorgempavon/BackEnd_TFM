package com.example.library.entities.model;

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
    @Column(name = "numpenalties")
    private Integer numPenalties;
    @OneToOne
    @JoinColumn(name = "adminid", referencedColumnName = "id", unique = true)
    private Admin admin;

    public Rule(){

    }

    public Rule(Integer numPenalties,Admin admin){
        this.numPenalties = numPenalties;
        this.admin = admin;
    }

    public Rule(Long id, Integer numPenalties,Admin admin){
        this.id = id;
        this.numPenalties = numPenalties;
        this.admin = admin;
    }


}
