package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.book.BookCreateDTO;
import com.example.library.entities.dto.book.BookDTO;
import com.example.library.entities.dto.book.BookUpdateDTO;
import com.example.library.entities.model.Book;
import com.example.library.entities.repository.BookRepository;
import com.example.library.util.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }
    @Transactional
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
        spec = ValidationUtils.buildQueryBookStringByField(spec,"title",title);
        spec = ValidationUtils.buildQueryBookStringByField(spec,"author",author);
        spec = ValidationUtils.buildQueryBookStringByField(spec,"isbn",isbn);
        spec = ValidationUtils.buildQueryBookStringByField(spec,"genre",genre);

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

    public BookDTO update(Long id,BookUpdateDTO bookUpdateDTO){
        boolean existsIsbn = this.bookRepository.existsByIsbn(bookUpdateDTO.getIsbn());
        if(existsIsbn){
            Book existingBookWithIsbn = this.bookRepository.findByIsbn(bookUpdateDTO.getIsbn()).get();
            if (!existingBookWithIsbn.getId().equals(id)){
                throw new BadRequestException("Ya existe un libro con el isbn: "+bookUpdateDTO.getIsbn());
            }
        }

        Book book = this.bookRepository.findById(id).get();
        updateBookData(book,bookUpdateDTO);
        this.bookRepository.save(book);

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

    public void updateBookData(Book book, BookUpdateDTO bookUpdateDTO){
        String newIsbn =  bookUpdateDTO.getIsbn();
        String newTitle=  bookUpdateDTO.getTitle();
        Integer newStock =  bookUpdateDTO.getStock();
        Date newReleaseDate =  bookUpdateDTO.getReleaseDate();
        String newAuthor =  bookUpdateDTO.getAuthor();
        String newGenre =  bookUpdateDTO.getGenre();

        if(ValidationUtils.isValidAndChangedString(newIsbn,book.getIsbn())){
            book.setIsbn(newIsbn);
        }
        if(ValidationUtils.isValidAndChangedString(newTitle,book.getTitle())){
            book.setTitle(newTitle);
        }
        if(ValidationUtils.isValidAndChangedInteger(newStock,book.getStock())){
            book.setStock(newStock);
        }
        if(ValidationUtils.isValidAndChangedDate(newReleaseDate,book.getReleaseDate())){
            book.setReleaseDate(newReleaseDate);
        }
        if(ValidationUtils.isValidAndChangedString(newAuthor,book.getAuthor())){
            book.setAuthor(newAuthor);
        }
        if(ValidationUtils.isValidAndChangedString(newGenre,book.getGenre())){
            book.setGenre(newGenre);
        }
        
    }
    public String getBookTitleByBook(Book book){
        if (!this.bookRepository.existsById(book.getId())){
            throw new NotFoundException("No existe el libro con el id proporcionado");
        }
        return book.getTitle();
    }

    public Book getBookByBookId(Long bookId){
        if (!this.bookRepository.existsById(bookId)){
            throw new NotFoundException("No existe el libro con el id proporcionado");
        }
        return this.bookRepository.findById(bookId).get();
    }
}
