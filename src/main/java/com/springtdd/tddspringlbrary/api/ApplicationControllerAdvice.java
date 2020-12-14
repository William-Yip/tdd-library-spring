package com.springtdd.tddspringlbrary.api;

import com.springtdd.tddspringlbrary.api.exceptions.ApiErrors;
import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatus(ResponseStatusException e) {
        return new ResponseEntity(new ApiErrors(e), e.getStatus());
    }

}
