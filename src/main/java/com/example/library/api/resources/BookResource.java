package com.example.library.api.resources;


import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.services.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bibliokie/books")
@SecurityRequirement(name = "bearerAuth")
public class BookResource {

    private final BookService bookService;

    public BookResource(BookService bookService){
        this.bookService = bookService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody BookCreateDTO bookCreateDTO) {
        BookDTO responseBookDTO = this.bookService.create(bookCreateDTO);
        URI location = URI.create("/books/" + responseBookDTO.getId());
        return ResponseEntity.created(location).body(responseBookDTO);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<?> findById(@PathVariable Long id){
        BookDTO responseBookDTO= this.bookService.findById(id);
        return ResponseEntity.ok(responseBookDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public  ResponseEntity<?> delete(@PathVariable Long id){
        this.bookService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public  ResponseEntity<?> findByTitleAndAuthorAndIsbnAndGenre(@RequestParam(required = false) String title,
                                                       @RequestParam(required = false) String author,
                                                       @RequestParam(required = false) String isbn,
                                                       @RequestParam(required = false) String genre){

        List<BookDTO> responseListBookDTO = this.bookService.findByTitleAndAuthorAndIsbnAndGenre(title,author,isbn,genre);
        return ResponseEntity.ok(responseListBookDTO);
    }
}
