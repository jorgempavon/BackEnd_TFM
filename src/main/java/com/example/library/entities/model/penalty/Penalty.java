package com.example.library.entities.model.penalty;

import com.example.library.entities.model.BookingLoan;
import com.example.library.entities.model.user.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "penalty", schema = "library")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "justificationpenalty")
    private String justificationPenalty;
    @Column(name = "fulfilled", nullable = false)
    private Boolean fulfilled;
    @Column(name = "forgived", nullable = false)
    private Boolean forgived;
    @Column(name = "creationdate", nullable = false)
    private Date creationDate;
    @Column(name = "type",nullable = false)
    private String type;
    @OneToOne
    @JoinColumn(name = "bookingloanid", referencedColumnName = "id", unique = true)
    private BookingLoan bookingLoan;
    @OneToOne
    @JoinColumn(name = "clientid", referencedColumnName = "id", unique = true)
    private Client client;

    public Penalty(){

    }

    public Penalty(String description,String justificationPenalty,Boolean fulfilled,
                   Boolean forgived,String type, BookingLoan bookingLoan, Client client){
        this.description = description;
        this.justificationPenalty = justificationPenalty;
        this.fulfilled = fulfilled;
        this.forgived = forgived;
        this.bookingLoan = bookingLoan;
        this.client = client;
        this.type = type;
        this.creationDate = new Date();
    }

    public Penalty(Long id, String description,String justificationPenalty,Boolean fulfilled, Boolean forgived,String type , BookingLoan bookingLoan, Client client){
        this.id = id;
        this.description = description;
        this.justificationPenalty = justificationPenalty;
        this.fulfilled = fulfilled;
        this.forgived = forgived;
        this.bookingLoan = bookingLoan;
        this.client = client;
        this.type = type;
    }

}
