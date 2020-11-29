package com.springtdd.tddspringlbrary.api.exceptions;

import com.springtdd.tddspringlbrary.exceptions.BussinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {

    private List<String> erros = new ArrayList<>();

    public ApiErrors(BindingResult bindingResults) {
        bindingResults.getAllErrors().forEach(error -> erros.add(error.getDefaultMessage()));
    }

    public ApiErrors(BussinessException e) {
        String message = e.getMessage();
        erros.add(message);
    }

    public List<String> getErros() {
        return this.erros;
    }

}
