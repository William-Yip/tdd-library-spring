package com.springtdd.tddspringlbrary.api.resource;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.exceptions.ApiErrors;
import com.springtdd.tddspringlbrary.api.resource.dto.BookDTO;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);
        Book savedBook = service.save(entity);
        BookDTO savedDTO = modelMapper.map(savedBook, BookDTO.class);
        return savedDTO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors invalidBody(MethodArgumentNotValidException e) {
        BindingResult bindingResults = e.getBindingResult();
        return new ApiErrors(bindingResults);
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors duplicatedIsbn(BussinessException e) {
        return new ApiErrors(e);
    }

}
