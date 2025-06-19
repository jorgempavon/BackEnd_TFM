package com.example.library.api.resources;


import com.example.library.entities.dto.BookCreateDTO;
import com.example.library.entities.dto.BookDTO;
import com.example.library.services.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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
}
