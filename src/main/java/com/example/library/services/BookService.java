package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.repository.BookRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public BookDTO create(BookCreateDTO bookCreateDTO){
        boolean existsIsbn = this.bookRepository.existsByIsbn(bookCreateDTO.getIsbn());
        if(existsIsbn){
            throw new BadRequestException("Ya existe un libro con el isbn: "+bookCreateDTO.getIsbn());
        }
        String qr = UUID.randomUUID().toString();
        Book newBook = new Book(
                bookCreateDTO.getIsbn(),
                bookCreateDTO.getTitle(),
                qr,
                bookCreateDTO.getReleaseDate(),
                bookCreateDTO.getStock(),
                bookCreateDTO.getGenre(),
                bookCreateDTO.getAuthor()
        );
        this.bookRepository.save(newBook);

        return new BookDTO(
                newBook.getId(),
                newBook.getIsbn(),
                newBook.getTitle(),
                newBook.getQr(),
                newBook.getReleaseDate(),
                newBook.getStock(),
                newBook.getGenre(),
                newBook.getAuthor()
        );
    }
    public BookDTO findById(Long id){
        if(!this.bookRepository.existsById(id)){
            throw new NotFoundException("No existe ningún libro con el id: "+id.toString());
        }
        Book book = this.bookRepository.findById(id).get();
        return new BookDTO(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getQr(),
                book.getReleaseDate(),
                book.getStock(),
                book.getGenre(),
                book.getAuthor()
        );
    }
    public void delete(Long id){
        if(this.bookRepository.existsById(id)){
            Book book = this.bookRepository.findById(id).get();
            this.bookRepository.delete(book);
        }
    }

    public List<BookDTO> findByTitleAndAuthorAndIsbnAndGenre(String title,String author,String isbn,String genre){
        Specification<Book> spec = Specification.where(null);

        if (title != null && !title.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%")
            );
        }

        if (author != null && !author.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%")
            );
        }

        if (isbn != null && !isbn.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("isbn")), "%" + isbn.toLowerCase() + "%")
            );
        }

        if (genre != null && !genre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%")
            );
        }

        List<Book> books = this.bookRepository.findAll(spec);
        List<BookDTO> responseList = new ArrayList<>();

        for (Book book : books) {
            BookDTO newBookDto = new BookDTO(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getQr(),
                    book.getReleaseDate(),
                    book.getStock(),
                    book.getGenre(),
                    book.getAuthor()
            );
            responseList.add(newBookDto);
        }
        return responseList;
    }
}
