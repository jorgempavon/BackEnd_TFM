package com.example.library.entities.model.rule;

import com.example.library.entities.model.rule.Rule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bookingperiodrule", schema = "library")
public class BookingPeriodRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ruleid", referencedColumnName = "id", unique = true)
    private Rule rule;

    public BookingPeriodRule(){
    }

    public BookingPeriodRule(Long id,Rule rule){
        this.id = id;
        this.rule = rule;
    }

    public BookingPeriodRule(Rule rule){
        this.rule = rule;
    }

}
