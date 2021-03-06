package com.springtdd.tddspringlbrary.api.service;

import com.springtdd.tddspringlbrary.api.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageable);

    Optional<Book> getBookByIsbn(String isbn);
}
