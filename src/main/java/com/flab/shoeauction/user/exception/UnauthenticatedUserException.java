package com.flab.shoeauction.user.exception;

public class UnauthenticatedUserException extends RuntimeException {
  public UnauthenticatedUserException(String message){
    super(message);
  }

}
