package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.repository.BookRepository;
import org.springframework.stereotype.Service;

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
        if(this.bookRepository.existsById(id)){
            throw new NotFoundException("No existe ningún libro con el id: "+id);
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
}
