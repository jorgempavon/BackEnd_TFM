package com.example.library.entities.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class BookCreateDTO {
    @Size(min = 10, max = 13, message = "El isbn debe tener entre 10 y 13 caracteres")
    @NotBlank(message = "El isbn es obligatorio")
    private String isbn;
    @NotBlank(message = "El title es obligatorio")
    private String title;
    private Date releaseDate;
    @NotNull(message = "El stock es obligatorio")
    private Integer stock;
    @NotBlank(message = "El genre es obligatorio")
    private String genre;
    @NotBlank(message = "El author es obligatorio")
    private String author;

    public BookCreateDTO(String isbn,String title, Date releaseDate, Integer stock, String genre, String author){
        this.isbn = isbn;
        this.title = title;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.genre = genre;
        this.author = author;
    }
}
