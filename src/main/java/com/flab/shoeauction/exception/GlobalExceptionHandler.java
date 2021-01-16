package com.flab.shoeauction.exception;

import com.flab.shoeauction.exception.smsCetification.SmsSendFailedException;
import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.util.response.ResponseConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> duplicateEmailException(DuplicateEmailException exception) {
        log.error("중복된 이메일입니다.", exception);
        return ResponseConstants.DUPLICATION_EMAIL_RESPONSE;
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<String> duplicateNicknameException(DuplicateNicknameException exception) {
        log.error("중복된 닉네임입니다.", exception);
        return ResponseConstants.DUPLICATION_NICKNAME_RESPONSE;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getFieldError().getDefaultMessage(), exception);
        return new ResponseEntity<>(exception.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SmsSendFailedException.class)
    public ResponseEntity<String> smsSendFailedException(SmsSendFailedException exception) {
        log.error("SMS 발송이 실패하였습니다.", exception);
        return ResponseConstants.SMS_SEND_FAILED;
    }
}