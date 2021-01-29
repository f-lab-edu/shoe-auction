package com.flab.shoeauction.exception;

import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.DUPLICATION_EMAIL;
import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.DUPLICATION_NICKNAME;
import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_ENTITY_UNAUTHORIZED;
import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_LOGIN_UNAUTHORIZED;

import com.flab.shoeauction.exception.user.EmailDuplicateException;
import com.flab.shoeauction.exception.user.NicknameDuplicateException;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class CustomizeResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailDuplicateException.class)
    public final ResponseEntity<String> handleEmailDuplicateException(EmailDuplicateException ex,
        WebRequest request) {
        log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return DUPLICATION_EMAIL;
    }

    @ExceptionHandler(NicknameDuplicateException.class)
    public final ResponseEntity<String> handleNicknameDuplicatedException(
        NicknameDuplicateException ex, WebRequest request) {
        log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return DUPLICATION_NICKNAME;
    }


    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException ex, WebRequest request) {
        log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return RESPONSE_ENTITY_UNAUTHORIZED;
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public final ResponseEntity<String> handleUnauthenticatedUserException(
        UnauthenticatedUserException ex, WebRequest request) {
        log.error("Failed to Execution ::  {}, detection time={} ", request.getDescription(false),
            LocalDateTime.now(), ex);
        return RESPONSE_LOGIN_UNAUTHORIZED;
    }

}
