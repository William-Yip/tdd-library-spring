package com.springtdd.tddspringlbrary.api.resource;

import com.springtdd.tddspringlbrary.api.entity.Book;
import com.springtdd.tddspringlbrary.api.entity.Loan;
import com.springtdd.tddspringlbrary.api.resource.dto.LoanDTO;
import com.springtdd.tddspringlbrary.api.service.BookService;
import com.springtdd.tddspringlbrary.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final BookService bookService;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long makeALoan(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan savedOne = loanService.save(
                Loan.builder()
                        .id(1l)
                        .customer("Fulano")
                        .loanDate(LocalDate.now())
                        .book(book)
                        .build()
        );

        return savedOne.getId();
    }

}
