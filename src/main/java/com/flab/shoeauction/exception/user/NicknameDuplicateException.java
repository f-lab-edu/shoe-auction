package com.flab.shoeauction.exception.user;

public class NicknameDuplicateException extends RuntimeException {

    public NicknameDuplicateException(String message) {
        super(message);
    }
}