package com.springtdd.tddspringlbrary.api.resource;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.resource.dto.BookDTO;
import com.springtdd.tddspringlbrary.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("{id}")
    public BookDTO getBookById(@PathVariable Long id) {
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class) )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("{id}")
    public BookDTO udpateBook(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {
        return service.getById(id)
                .map(book -> {
                    book.setAuthor(dto.getAuthor());
                    book.setTitle(dto.getTitle());
                    book.setIsbn(dto.getIsbn());
                    service.update(book);
                    return modelMapper.map(book, BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> filterBooks(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }

}
