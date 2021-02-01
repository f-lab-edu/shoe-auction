package com.flab.shoeauction.exception;

import static com.flab.shoeauction.common.util.response.ResponseConstants.LOGIN_UNAUTHORIZED;
import static com.flab.shoeauction.common.util.response.ResponseConstants.UNAUTHORIZED;

import com.flab.shoeauction.common.util.response.ResponseConstants;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> duplicateEmailException(DuplicateEmailException exception) {
        log.error("중복된 이메일입니다.", exception);
        return ResponseConstants.DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<String> duplicateNicknameException(DuplicateNicknameException exception) {
        log.error("중복된 닉네임입니다.", exception);
        return ResponseConstants.DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getFieldError().getDefaultMessage(), exception);
        return new ResponseEntity<>(exception.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
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

}