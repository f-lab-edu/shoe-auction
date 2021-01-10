package com.flab.shoeauction.exception;

import com.flab.shoeauction.exception.user.DuplicateEmailException;
import com.flab.shoeauction.exception.user.DuplicateNicknameException;
import com.flab.shoeauction.exception.user.WrongConfirmPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    protected ResponseEntity<String> duplicateEmailException(DuplicateEmailException e) {
        return new ResponseEntity<String>("중복된 이메일입니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    protected ResponseEntity<String> duplicateNicknameException(DuplicateNicknameException e) {
        return new ResponseEntity<String>("중복된 닉네임입니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongConfirmPasswordException.class)
    protected ResponseEntity<String> wrongConfirmPasswordException(WrongConfirmPasswordException e) {
        return new ResponseEntity<String>("비밀번호 확인이 맞지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<String>(e.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }
}