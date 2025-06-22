package com.example.library.entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "temporaryperiodrule", schema = "library")
public class TemporaryPeriodRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ruleid", referencedColumnName = "id", unique = true)
    private Rule rule;
}
