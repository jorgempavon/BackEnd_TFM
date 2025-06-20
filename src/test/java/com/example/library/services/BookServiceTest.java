package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
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
    private static final Long exampleId = 4L;
    private static final String exampleQr= "d9b2d63d-a233-4123-847a-7ac09a557b05\n";
    private static final String exampleIsbn = "9781234567890";
    private static final String exampleTitle= "9781234567890";
    private static final Integer exampleStock = 20;
    private static final Date exampleReleaseDate = new Date();
    private static final String exampleGenre = "9781234567890";
    private static final String exampleAuthor = "9781234567890";

    private static final Book book = new Book(
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
        when(this.bookRepository.existsByIsbn(exampleIsbn)).thenReturn(false);

        BookDTO resultBookDTO = this.bookService.create(newBookCreateDTO);

        assertEquals(resultBookDTO.getIsbn(), exampleIsbn);
        assertEquals(resultBookDTO.getTitle(), exampleTitle);
        assertEquals(resultBookDTO.getReleaseDate(), exampleReleaseDate);
        assertEquals(resultBookDTO.getStock(), exampleStock);
        assertEquals(resultBookDTO.getGenre(), exampleGenre);
        assertEquals(resultBookDTO.getAuthor(), exampleAuthor);
    }

    @Test
    void createBook_whenExistsBook_throwsBadRequestException(){
        BookCreateDTO newBookCreateDTO = new BookCreateDTO(
                exampleIsbn,
                exampleTitle,
                exampleReleaseDate,
                exampleStock,
                exampleGenre,
                exampleAuthor
        );
        when(this.bookRepository.existsByIsbn(exampleIsbn)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            bookService.create(newBookCreateDTO);
        });
    }
    @Test
    void findById_successful(){
        when(this.bookRepository.existsById(exampleId)).thenReturn(true);
        when(this.bookRepository.findById(exampleId)).thenReturn(Optional.of(book));

        BookDTO resultBookDTO = bookService.findById(exampleId);
        assertEquals(resultBookDTO.getIsbn(), exampleIsbn);
        assertEquals(resultBookDTO.getTitle(), exampleTitle);
        assertEquals(resultBookDTO.getReleaseDate(), exampleReleaseDate);
        assertEquals(resultBookDTO.getStock(), exampleStock);
        assertEquals(resultBookDTO.getGenre(), exampleGenre);
        assertEquals(resultBookDTO.getAuthor(), exampleAuthor);
    }
    @Test
    void findById_whenNotExistsBook_throwsNotFoundException(){
        when(this.bookRepository.existsById(exampleId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            bookService.findById(exampleId);
        });
    }
    @Test
    void delete_successful_whenExistsBook(){
        when(this.bookRepository.existsById(exampleId)).thenReturn(true);
        when(this.bookRepository.findById(exampleId)).thenReturn(Optional.of(book));
        this.bookService.delete(exampleId);
    }

    @Test
    void delete_successful_whenNotExistsBook(){
        when(this.bookRepository.existsById(exampleId)).thenReturn(false);
        this.bookService.delete(exampleId);
    }

    @Test
    void findByTitleAndAuthorAndIsbnAndGenre(){
        List<Book> mockBooks = List.of(book);

        when(bookRepository.findAll(any(Specification.class))).thenReturn(mockBooks);

        List<BookDTO> result = bookService.findByTitleAndAuthorAndIsbnAndGenre(exampleTitle, exampleAuthor,exampleIsbn,exampleGenre);

        assertEquals(1, result.size());
        assertEquals(exampleTitle, result.get(0).getTitle());
        assertEquals(exampleAuthor, result.get(0).getAuthor());
        assertEquals(exampleIsbn, result.get(0).getIsbn());
        assertEquals(exampleGenre, result.get(0).getGenre());
    }
}
