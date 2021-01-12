package com.flab.shoeauction.common.exception;

import com.flab.shoeauction.user.exception.PasswordMissMatchException;
import com.flab.shoeauction.user.exception.UserDuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomizeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserDuplicateException.class)
    public final ResponseEntity<Object> handleUserDuplicateException(UserDuplicateException ex, WebRequest request) {
        log.error(ex.getMessage());
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(exceptionResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordMissMatchException.class)
    public final ResponseEntity<Object> handlePasswordMissMatchException(PasswordMissMatchException ex, WebRequest request) {

        log.error(ex.getMessage());
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}