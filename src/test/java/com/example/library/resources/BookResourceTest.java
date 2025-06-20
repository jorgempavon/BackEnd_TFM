package com.example.library.resources;

import com.example.library.api.resources.BookResource;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
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

    private static final BookDTO newBookDTO = new BookDTO(
            exampleId,
            exampleIsbn,
            exampleTitle,
            exampleQr,
            exampleReleaseDate,
            exampleStock,
            exampleGenre,
            exampleAuthor
    );

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

        when(this.bookService.create(newBookCreateDTO)).thenReturn(newBookDTO);

        ResponseEntity<?> responseCreateBook = this.bookResource.create(newBookCreateDTO);

        assertEquals(HttpStatus.CREATED, responseCreateBook.getStatusCode());
        assertTrue(responseCreateBook.getBody() instanceof BookDTO);
    }

    @Test
    void findById_successful(){
        when(this.bookService.findById(exampleId)).thenReturn(newBookDTO);

        ResponseEntity<?> responseFindBook = this.bookResource.findById(exampleId);

        assertEquals(HttpStatus.OK, responseFindBook.getStatusCode());
        assertTrue(responseFindBook.getBody() instanceof BookDTO);
    }

    @Test
    void delete_successful(){
        doNothing().when(this.bookService).delete(exampleId);
        ResponseEntity<?> responseDeleteBook = this.bookResource.delete(exampleId);
        assertEquals(HttpStatus.OK, responseDeleteBook.getStatusCode());
    }

    @Test
    void findByTitleAndAuthorAndIsbnAndGenre(){
        List<BookDTO> listBooksDto = List.of(newBookDTO);

        when(this.bookService.findByTitleAndAuthorAndIsbnAndGenre(exampleTitle,exampleAuthor,exampleIsbn,exampleGenre))
                .thenReturn(listBooksDto);

        ResponseEntity<?> result = bookResource.findByTitleAndAuthorAndIsbnAndGenre(exampleTitle,exampleAuthor,exampleIsbn,exampleGenre);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listBooksDto);
    }

}
