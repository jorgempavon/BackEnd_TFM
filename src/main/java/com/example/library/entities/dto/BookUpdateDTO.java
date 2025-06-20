package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.Size;

import java.util.Date;
@AtLeastOneField(fields = {"isbn","title","releaseDate","stock","genre","author"})
public class BookUpdateDTO {

    @Size(min = 10, max = 13, message = "El isbn debe tener entre 10 y 13 caracteres")
    private String isbn;

    private String title;
    private Date releaseDate;
    private Integer stock;
    private String genre;
    private String author;
}
