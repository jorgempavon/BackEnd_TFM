package com.example.library.entities.dto.book;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BookDTO {
    private Long id;
    @NotBlank(message = "El isbn es obligatorio")
    private String isbn;
    @NotBlank(message = "El title es obligatorio")
    private String title;
    @NotBlank(message = "El qr es obligatorio")
    private String qr;
    private Date releaseDate;
    @NotNull(message = "El stock es obligatorio")
    private Integer stock;
    @NotBlank(message = "El genre es obligatorio")
    private String genre;
    @NotBlank(message = "El author es obligatorio")
    private String author;

    public BookDTO(Long id,String isbn,String title,String qr, Date releaseDate, Integer stock, String genre, String author){
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
