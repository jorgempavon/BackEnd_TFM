package com.example.library.entities.model.penalty;

import com.example.library.entities.model.rule.BookingPeriodRule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bookingperiodpenalty", schema = "library")
public class BookingPeriodPenalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "days", nullable = false)
    private Integer days;
    @OneToOne
    @JoinColumn(name = "penaltyid", referencedColumnName = "id", unique = true)
    private Penalty penalty;

    @OneToOne
    @JoinColumn(name = "bookingperiodruleid", referencedColumnName = "id", unique = true)
    private BookingPeriodRule bookingPeriodRule;

    public BookingPeriodPenalty(){}
    public BookingPeriodPenalty(Long id,Integer days,Penalty penalty,BookingPeriodRule bookingPeriodRule){
        this.id = id;
        this.days = days;
        this.penalty = penalty;
        this.bookingPeriodRule = bookingPeriodRule;
    }
}
