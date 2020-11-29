package com.springtdd.tddspringlbrary.api.service;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.dto.BookDTO;

public interface BookService {

    Book save(Book book);
}
