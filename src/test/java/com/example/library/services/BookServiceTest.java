package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.entities.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    private static final String exampleIsbn = "9781234567890";
    private static final String exampleTitle= "9781234567890";
    private static final Integer exampleStock = 20;
    private static final Date exampleReleaseDate = new Date();
    private static final String exampleGenre = "9781234567890";
    private static final String exampleAuthor = "9781234567890";

    @Test
    void createBook_successful(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                exampleIsbn,
                exampleTitle,
                exampleReleaseDate,
                exampleStock,
                exampleGenre,
                exampleAuthor
        );
        when(this.bookRepository.existsByIsbn(exampleIsbn)).thenReturn(false);

        BookDTO resultBookDTO = this.bookService.create(newBookCreateDTO);

        assertEquals(resultBookDTO.getIsbn(), exampleIsbn);
        assertEquals(resultBookDTO.getTitle(), exampleTitle);
        assertEquals(resultBookDTO.getReleaseDate(), exampleReleaseDate);
        assertEquals(resultBookDTO.getStock(), exampleStock);
        assertEquals(resultBookDTO.getGenre(), exampleGenre);
        assertEquals(resultBookDTO.getAuthor(), exampleAuthor);
    }
}
