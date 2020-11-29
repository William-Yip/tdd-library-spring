package com.springtdd.tddspringlbrary.api.resource.service.impl;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.model.repository.BookRepository;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImp implements BookService {

    private BookRepository repository;

    public BookServiceImp(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn()))
            throw new BussinessException("Isbn duplicado");
        return repository.save(book);
    }

}
