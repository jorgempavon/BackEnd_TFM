package com.example.library.resources;

import com.example.library.api.resources.BookResource;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.entities.dto.BookUpdateDTO;
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

    private static final Long EXAMPLE_ID = 4L;

    private static final String EXAMPLE_ISBN = "9781234567890";
    private static final String EXAMPLE_TITLE= "El Misterio del Bosque";
    private static final String EXAMPLE_QR= "d9b2d63d-a233-4123-847a-7ac09a557b05\n";
    private static final Integer EXAMPLE_STOCK = 20;
    private static final Date EXAMPLE_RELEASE_DATE = new Date();
    private static final String EXAMPLE_GENRE = "Ficción";
    private static final String EXAMPLE_AUTHOR = "Laura Márquez";

    private static final String EXAMPLE_OTHER_ISBN = "9781234567893";
    private static final String EXAMPLE_OTHER_TITLE= "El Tiempo Expandido";
    private static final Integer EXAMPLE_OTHER_STOCK = 10;
    private static final Date EXAMPLE_OTHER_RELEASE_DATE = new Date();
    private static final String EXAMPLE_OTHER_GENRE = "Fantasía";
    private static final String EXAMPLE_OTHER_AUTHOR = "María Torres";

    private static final BookDTO NEW_BOOK_DTO = new BookDTO(
            EXAMPLE_ID,
            EXAMPLE_ISBN,
            EXAMPLE_TITLE,
            EXAMPLE_QR,
            EXAMPLE_RELEASE_DATE,
            EXAMPLE_STOCK,
            EXAMPLE_GENRE,
            EXAMPLE_AUTHOR
    );

    private static final BookDTO OTHER_BOOK_DTO = new BookDTO(
            EXAMPLE_ID,
            EXAMPLE_OTHER_ISBN,
            EXAMPLE_OTHER_TITLE,
            EXAMPLE_QR,
            EXAMPLE_OTHER_RELEASE_DATE,
            EXAMPLE_OTHER_STOCK,
            EXAMPLE_OTHER_GENRE,
            EXAMPLE_OTHER_AUTHOR
    );
    private static final BookUpdateDTO BOOK_UPDATE_DTO = new BookUpdateDTO(
            EXAMPLE_OTHER_ISBN,EXAMPLE_OTHER_TITLE,EXAMPLE_OTHER_RELEASE_DATE,
            EXAMPLE_OTHER_STOCK,EXAMPLE_OTHER_GENRE,EXAMPLE_OTHER_AUTHOR
    );

    @Test
    void createBook_successful(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                EXAMPLE_ISBN,
                EXAMPLE_TITLE,
                EXAMPLE_RELEASE_DATE,
                EXAMPLE_STOCK,
                EXAMPLE_GENRE,
                EXAMPLE_AUTHOR
        );

        when(this.bookService.create(newBookCreateDTO)).thenReturn(NEW_BOOK_DTO);

        ResponseEntity<?> responseCreateBook = this.bookResource.create(newBookCreateDTO);

        assertEquals(HttpStatus.CREATED, responseCreateBook.getStatusCode());
        assertTrue(responseCreateBook.getBody() instanceof BookDTO);
    }

    @Test
    void findById_successful(){
        when(this.bookService.findById(EXAMPLE_ID)).thenReturn(NEW_BOOK_DTO);

        ResponseEntity<?> responseFindBook = this.bookResource.findById(EXAMPLE_ID);

        assertEquals(HttpStatus.OK, responseFindBook.getStatusCode());
        assertTrue(responseFindBook.getBody() instanceof BookDTO);
    }

    @Test
    void delete_successful(){
        doNothing().when(this.bookService).delete(EXAMPLE_ID);
        ResponseEntity<?> responseDeleteBook = this.bookResource.delete(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, responseDeleteBook.getStatusCode());
    }

    @Test
    void findByTitleAndAuthorAndIsbnAndGenre(){
        List<BookDTO> listBooksDto = List.of(NEW_BOOK_DTO);

        when(this.bookService.findByTitleAndAuthorAndIsbnAndGenre(EXAMPLE_TITLE,EXAMPLE_AUTHOR,EXAMPLE_ISBN,EXAMPLE_GENRE))
                .thenReturn(listBooksDto);

        ResponseEntity<?> result = bookResource.findByTitleAndAuthorAndIsbnAndGenre(EXAMPLE_TITLE,EXAMPLE_AUTHOR,EXAMPLE_ISBN,EXAMPLE_GENRE);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listBooksDto);
    }

    @Test
    void update_successful(){
        when(this.bookService.update(EXAMPLE_ID,BOOK_UPDATE_DTO)).thenReturn(OTHER_BOOK_DTO);
        ResponseEntity<?> result = bookResource.update(EXAMPLE_ID,BOOK_UPDATE_DTO);
        BookDTO resultBody = (BookDTO) result.getBody();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resultBody.getId(),OTHER_BOOK_DTO.getId());
        assertEquals(resultBody.getIsbn(),OTHER_BOOK_DTO.getIsbn());
        assertEquals(resultBody.getTitle(),OTHER_BOOK_DTO.getTitle());
        assertEquals(resultBody.getStock(),OTHER_BOOK_DTO.getStock());
        assertEquals(resultBody.getAuthor(),OTHER_BOOK_DTO.getAuthor());
        assertEquals(resultBody.getGenre(),OTHER_BOOK_DTO.getGenre());
    }
}
