package com.flab.shoeauction.exception;

import com.flab.shoeauction.common.util.response.ResponseConstants;
import com.flab.shoeauction.exception.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static com.flab.shoeauction.common.util.response.ResponseConstants.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> duplicateEmailException(DuplicateEmailException ex, WebRequest request) {
        log.debug("Duplicate email ::  {}, detection time={} ", request.getDescription(false),
                LocalDateTime.now(), ex);
        return ResponseConstants.DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<String> duplicateNicknameException(DuplicateNicknameException ex, WebRequest request) {
        log.debug("Duplicate nickname ::  {}, detection time={} ", request.getDescription(false),
                LocalDateTime.now(), ex);
        return ResponseConstants.DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getFieldError().getDefaultMessage(), ex);
        return new ResponseEntity<>(ex.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException ex, WebRequest request) {
        log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return UNAUTHORIZED;
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(
        UnauthenticatedUserException ex, WebRequest request) {
        log.error("Failed to Execution ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return LOGIN_UNAUTHORIZED;
    }

    @ExceptionHandler(WrongPasswordException.class)
    public final ResponseEntity<String> wrongPasswordException(WrongPasswordException ex, WebRequest request){
        log.debug("Wrong password ::  {}, detection time={} ", request.getDescription(false),
                LocalDateTime.now(), ex);
        return WRONG_PASSWORD;
    }
}