package com.example.library.resources;

import com.example.library.api.resources.BookResource;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookResourceTest {
    @Mock
    private BookService bookService;

    @InjectMocks
    private BookResource bookResource;

    private static final Long exampleId = 4L;

    private static final String exampleIsbn = "9781234567890";
    private static final String exampleTitle= "9781234567890";
    private static final String exampleQr= "d9b2d63d-a233-4123-847a-7ac09a557b05\n";
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
        BookDTO newBookDTO = new BookDTO(
                exampleId,
                exampleIsbn,
                exampleTitle,
                exampleQr,
                exampleReleaseDate,
                exampleStock,
                exampleGenre,
                exampleAuthor
        );
        when(this.bookService.create(newBookCreateDTO)).thenReturn(newBookDTO);

        BookDTO responseCreateBook = this.bookService.create(newBookCreateDTO);

        assertEquals(responseCreateBook.getIsbn(), exampleIsbn);
        assertEquals(responseCreateBook.getTitle(), exampleTitle);
        assertEquals(responseCreateBook.getReleaseDate(), exampleReleaseDate);
        assertEquals(responseCreateBook.getStock(), exampleStock);
        assertEquals(responseCreateBook.getGenre(), exampleGenre);
        assertEquals(responseCreateBook.getAuthor(), exampleAuthor);
    }

    @Test
    void createBook_whenExistsIsbn_throwsBadRequestException(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                exampleIsbn,
                exampleTitle,
                exampleReleaseDate,
                exampleStock,
                exampleGenre,
                exampleAuthor
        );
    }
}
