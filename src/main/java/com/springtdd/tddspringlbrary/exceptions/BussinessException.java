package com.springtdd.tddspringlbrary.exceptions;

public class BussinessException extends RuntimeException {

    public BussinessException(String errorMessage) {
        super(errorMessage);
    }

}
