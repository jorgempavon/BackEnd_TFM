package com.example.library.entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "bookingloan", schema = "library")
public class BookingLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "begindate", nullable = false )
    private Date beginDate;
    @Column(name = "enddate", nullable = false)
    private Date endDate;
    @Column(name = "collected", nullable = false)
    private Boolean collected;
    @Column(name = "returned", nullable = false)
    private Boolean returned;
    @OneToOne
    @JoinColumn(name = "bookid", referencedColumnName = "id", unique = true)
    private Book book;
    @OneToOne
    @JoinColumn(name = "clientid", referencedColumnName = "id", unique = true)
    private Client client;

    public BookingLoan(){

    }

    public BookingLoan(Date beginDate,Date endDate,Boolean collected, Boolean returned, Book book, Client client){
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.collected = collected;
        this.returned = returned;
        this.book = book;
        this.client = client;
    }

    public BookingLoan(Long id,Date beginDate,Date endDate,Boolean collected, Boolean returned, Book book, Client client){
        this.id = id;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.collected = collected;
        this.returned = returned;
        this.book = book;
        this.client = client;
    }

}
