package com.example.library.entities.repository;

import com.example.library.entities.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> , JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);
    Optional<Book> findByIsbn(String isbn);
}
