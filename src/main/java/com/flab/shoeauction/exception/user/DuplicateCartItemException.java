package com.flab.shoeauction.exception.user;

public class DuplicateCartItemException extends RuntimeException {

    public DuplicateCartItemException(String message) {
        super(message);
    }
}