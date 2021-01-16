package com.flab.shoeauction.common.exception;

import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_EMAIL_CONFLICT;
import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_ENTITY_UNAUTHORIZED;
import static com.flab.shoeauction.common.utils.httpStatus.ResponseConstants.RESPONSE_NICKNAME_CONFLICT;

import com.flab.shoeauction.user.exception.EmailDuplicateException;
import com.flab.shoeauction.user.exception.NicknameDuplicateException;
import com.flab.shoeauction.user.exception.UserNotFoundException;
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
    return RESPONSE_EMAIL_CONFLICT;
  }

  @ExceptionHandler(NicknameDuplicateException.class)
  public final ResponseEntity<String> handleNicknameDuplicatedException(
      NicknameDuplicateException ex, WebRequest request) {
    log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
        LocalDateTime.now(), ex);
    return RESPONSE_NICKNAME_CONFLICT;
  }


  @ExceptionHandler(UserNotFoundException.class)
  public final ResponseEntity<String> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {
    log.error("Failed to signUp ::  {}, detection time={} ", request.getDescription(false),
        LocalDateTime.now(), ex);
    return RESPONSE_ENTITY_UNAUTHORIZED;
  }

}
