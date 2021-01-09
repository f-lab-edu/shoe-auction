package com.flab.shoeauction.user.exception;

public class PasswordMissMatchException extends RuntimeException  {
    public PasswordMissMatchException(String message) {
        super(message);
    }
}
