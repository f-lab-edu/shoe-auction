package com.flab.shoeauction.user.exception;

public class EmailDuplicateException extends RuntimeException {

  public EmailDuplicateException(String message) {
    super(message);
  }
}