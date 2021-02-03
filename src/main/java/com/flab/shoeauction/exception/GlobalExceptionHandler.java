package com.flab.shoeauction.exception;

import static com.flab.shoeauction.common.utils.response.ResponseConstants.BAD_REQUEST;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_EMAIL;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.DUPLICATION_NICKNAME;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.LOGIN_UNAUTHORIZED;
import static com.flab.shoeauction.common.utils.response.ResponseConstants.USER_NOT_FOUND;

import com.flab.shoeauction.exception.user.AuthenticationNumberMismatchException;
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
    public final ResponseEntity<String> duplicateEmailException(DuplicateEmailException exception) {
        log.error("중복된 이메일입니다.", exception);
        return DUPLICATION_EMAIL;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public final ResponseEntity<String> duplicateNicknameException(DuplicateNicknameException exception) {
        log.error("중복된 닉네임입니다.", exception);
        return DUPLICATION_NICKNAME;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getFieldError().getDefaultMessage(), exception);
        return new ResponseEntity<>(exception.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException ex, WebRequest request) {
        log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(
        UnauthenticatedUserException ex, WebRequest request) {
        log.error("Failed to Execution ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return LOGIN_UNAUTHORIZED;
    }

    @ExceptionHandler(AuthenticationNumberMismatchException.class)
    public final ResponseEntity<Void> handleAuthenticationNumberMismatchException(
        AuthenticationNumberMismatchException ex, WebRequest request) {
        log.error("인증번호가 일치하지 않습니다 :  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return BAD_REQUEST;
    }

}