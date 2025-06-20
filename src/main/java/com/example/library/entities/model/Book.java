package com.example.library.entities.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "book", schema = "library")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "qr", nullable = false, unique = true)
    private String qr;
    @Column(name = "releasedate")
    private Date releaseDate;
    @Column(name = "stock", nullable = false)
    private Integer stock;
    @Column(name = "genre", nullable = false)
    private String genre;
    @Column(name = "author", nullable = false)
    private String author;

    public Book(){

    }

    public Book(String isbn,String title,String qr, Date releaseDate, Integer stock, String genre, String author){
        this.isbn = isbn;
        this.title = title;
        this.qr = qr;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.genre = genre;
        this.author = author;
    }

    public Book(Long id,String isbn,String title,String qr, Date releaseDate, Integer stock, String genre, String author){
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.qr = qr;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.genre = genre;
        this.author = author;
    }
}
