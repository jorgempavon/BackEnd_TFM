package com.example.library.entities.model.penalty;

import com.example.library.entities.model.rule.TemporaryPeriodRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "temporaryperiodpenalty", schema = "library")
public class TemporaryPeriodPenalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "enddate", nullable = false)
    private Date endDate;
    @OneToOne
    @JoinColumn(name = "penaltyid", referencedColumnName = "id", unique = true)
    private Penalty penalty;
    @OneToOne
    @JoinColumn(name = "temporaryperiodruleid", referencedColumnName = "id", unique = true)
    private TemporaryPeriodRule temporaryPeriodRule;

    public TemporaryPeriodPenalty(){}

    public TemporaryPeriodPenalty(Long id,Date endDate,Penalty penalty,TemporaryPeriodRule temporaryPeriodRule){
        this.id = id;
        this.endDate = endDate;
        this.penalty = penalty;
        this.temporaryPeriodRule = temporaryPeriodRule;
    }
}
