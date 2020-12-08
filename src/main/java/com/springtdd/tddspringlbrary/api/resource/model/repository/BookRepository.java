package com.springtdd.tddspringlbrary.api.resource.model.repository;

import com.springtdd.tddspringlbrary.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
    Optional<Book> findById(Long id);
}
