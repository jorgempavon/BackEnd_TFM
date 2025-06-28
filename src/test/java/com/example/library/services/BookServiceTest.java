package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.book.BookCreateDTO;
import com.example.library.entities.dto.book.BookDTO;
import com.example.library.entities.dto.book.BookUpdateDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;
    private static final Long BOOK_ID = 4L;
    private static final Long OTHER_BOOK_ID = 2L;
    private static final String BOOK_QR= "d9b2d63d-a233-4123-847a-7ac09a557b05\n";
    private static final String BOOK_ISBN = "9781234567890";
    private static final String BOOK_TITLE= "El Misterio del Bosque";
    private static final Integer BOOK_STOCK = 20;
    private static final Date BOOK_RELEASE_DATE = new Date();
    private static final String BOOK_GENRE = "Ficción";
    private static final String BOOK_AUTHOR = "Laura Márquez";

    private static final String OTHER_BOOK_ISBN = "9781234567893";
    private static final String OTHER_BOOK_TITLE= "El Tiempo Expandido";
    private static final Integer OTHER_BOOK_STOCK = 10;
    private static final Date OTHER_BOOK_RELEASE_DATE = new Date();
    private static final String OTHER_BOOK_GENRE = "Fantasía";
    private static final String OTHER_BOOK_AUTHOR = "María Torres";
    private static final Book BOOK = new Book(
            BOOK_ID,
            BOOK_ISBN,
            BOOK_TITLE,
            BOOK_QR,
            BOOK_RELEASE_DATE,
            BOOK_STOCK,
            BOOK_GENRE,
            BOOK_AUTHOR
    );

    private static final Book OTHER_BOOK = new Book(
            OTHER_BOOK_ID,
            BOOK_ISBN,
            BOOK_TITLE,
            BOOK_QR,
            BOOK_RELEASE_DATE,
            BOOK_STOCK,
            BOOK_GENRE,
            BOOK_AUTHOR
    );


    private static final BookUpdateDTO bookUpdateDTO = new BookUpdateDTO(
            OTHER_BOOK_ISBN,OTHER_BOOK_TITLE,OTHER_BOOK_RELEASE_DATE,
            OTHER_BOOK_STOCK,OTHER_BOOK_GENRE,OTHER_BOOK_AUTHOR
    );

    @Test
    void createBook_successful(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                BOOK_ISBN,
                BOOK_TITLE,
                BOOK_RELEASE_DATE,
                BOOK_STOCK,
                BOOK_GENRE,
                BOOK_AUTHOR
        );
        when(this.bookRepository.existsByIsbn(BOOK_ISBN)).thenReturn(false);

        BookDTO resultBookDTO = this.bookService.create(newBookCreateDTO);

        assertEquals(resultBookDTO.getIsbn(), BOOK_ISBN);
        assertEquals(resultBookDTO.getTitle(), BOOK_TITLE);
        assertEquals(resultBookDTO.getReleaseDate(), BOOK_RELEASE_DATE);
        assertEquals(resultBookDTO.getStock(), BOOK_STOCK);
        assertEquals(resultBookDTO.getGenre(), BOOK_GENRE);
        assertEquals(resultBookDTO.getAuthor(), BOOK_AUTHOR);
    }

    @Test
    void createBook_whenExistsBook_throwsBadRequestException(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                BOOK_ISBN,
                BOOK_TITLE,
                BOOK_RELEASE_DATE,
                BOOK_STOCK,
                BOOK_GENRE,
                BOOK_AUTHOR
        );
        when(this.bookRepository.existsByIsbn(BOOK_ISBN)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            bookService.create(newBookCreateDTO);
        });
    }
    @Test
    void findById_successful(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(true);
        when(this.bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(BOOK));

        BookDTO resultBookDTO = bookService.findById(BOOK_ID);
        assertEquals(resultBookDTO.getIsbn(), BOOK_ISBN);
        assertEquals(resultBookDTO.getTitle(), BOOK_TITLE);
        assertEquals(resultBookDTO.getReleaseDate(), BOOK_RELEASE_DATE);
        assertEquals(resultBookDTO.getStock(), BOOK_STOCK);
        assertEquals(resultBookDTO.getGenre(), BOOK_GENRE);
        assertEquals(resultBookDTO.getAuthor(), BOOK_AUTHOR);
    }
    @Test
    void findById_whenNotExistsBook_throwsNotFoundException(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            bookService.findById(BOOK_ID);
        });
    }
    @Test
    void delete_successful_whenExistsBook(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(true);
        when(this.bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(BOOK));
        this.bookService.delete(BOOK_ID);
    }

    @Test
    void delete_successful_whenNotExistsBook(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(false);
        this.bookService.delete(BOOK_ID);
    }

    @Test
    void findByTitleAndAuthorAndIsbnAndGenre(){
        List<Book> mockBooks = List.of(BOOK);

        when(bookRepository.findAll(any(Specification.class))).thenReturn(mockBooks);

        List<BookDTO> result = bookService.findByTitleAndAuthorAndIsbnAndGenre(BOOK_TITLE, BOOK_AUTHOR,BOOK_ISBN,BOOK_GENRE);

        assertEquals(1, result.size());
        assertEquals(BOOK_TITLE, result.get(0).getTitle());
        assertEquals(BOOK_AUTHOR, result.get(0).getAuthor());
        assertEquals(BOOK_ISBN, result.get(0).getIsbn());
        assertEquals(BOOK_GENRE, result.get(0).getGenre());
    }

    @Test
    void updateBook_successful(){
        when(bookRepository.existsByIsbn(OTHER_BOOK_ISBN)).thenReturn(false);
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(OTHER_BOOK));

        BookDTO responseUpdate = this.bookService.update(BOOK_ID,bookUpdateDTO);

        assertEquals(responseUpdate.getIsbn(), OTHER_BOOK_ISBN);
        assertEquals(responseUpdate.getTitle(), OTHER_BOOK_TITLE);
        assertEquals(responseUpdate.getReleaseDate(), OTHER_BOOK_RELEASE_DATE);
        assertEquals(responseUpdate.getStock(), OTHER_BOOK_STOCK);
        assertEquals(responseUpdate.getGenre(), OTHER_BOOK_GENRE);
        assertEquals(responseUpdate.getAuthor(), OTHER_BOOK_AUTHOR);
    }

    @Test
    void updateBook_whenExistsOtherBookWithIsbn_throwsBadRequestException(){
        when(bookRepository.existsByIsbn(OTHER_BOOK_ISBN)).thenReturn(true);
        when(bookRepository.findByIsbn(OTHER_BOOK_ISBN)).thenReturn(Optional.of(OTHER_BOOK));
        assertThrows(BadRequestException.class, () -> {
            bookService.update(BOOK_ID,bookUpdateDTO);
        });
    }

    @Test
    void getBookTitleByBook_successful(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(true);
        String response = this.bookService.getBookTitleByBook(BOOK);
        assertEquals(response,BOOK_TITLE);
    }
    @Test
    void getBookTitleByBook_whenNotExists_throwNotFoundException(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            bookService.getBookTitleByBook(BOOK);
        });
    }
    @Test
    void getBookByBookId_successful(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(true);
        when(this.bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(BOOK));
        Book response = this.bookService.getBookByBookId(BOOK_ID);
        assertEquals(response,BOOK);
    }
    @Test
    void getBookByBook_whenNotExists_throwNotFoundException(){
        when(this.bookRepository.existsById(BOOK_ID)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            bookService.getBookByBookId(BOOK_ID);
        });
    }
}
