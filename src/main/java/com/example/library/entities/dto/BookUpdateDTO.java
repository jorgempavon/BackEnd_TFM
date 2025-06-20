package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AtLeastOneField(fields = {"isbn","title","releaseDate","stock","genre","author"})
public class BookUpdateDTO {

    @Size(min = 10, max = 13, message = "El isbn debe tener entre 10 y 13 caracteres")
    private String isbn;

    private String title;
    private Date releaseDate;
    private Integer stock;
    private String genre;
    private String author;

    public BookUpdateDTO(){}

    public BookUpdateDTO(String isbn, String title, Date releaseDate,Integer stock, String genre, String author){
        this.isbn = isbn;
        this.title = title;
        this.releaseDate = releaseDate;
        this.stock = stock;
        this.genre = genre;
        this.author = author;
    }
}
