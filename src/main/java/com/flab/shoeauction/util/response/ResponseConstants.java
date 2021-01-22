package com.flab.shoeauction.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {

    public static final ResponseEntity<Void> OK =
            ResponseEntity.ok().build();

    public static final ResponseEntity<Void> CREATED =
            ResponseEntity.status(HttpStatus.CREATED).build();

    public static final ResponseEntity<String> DUPLICATION_EMAIL =
            new ResponseEntity<>("중복된 이메일입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> DUPLICATION_NICKNAME =
            new ResponseEntity<>("중복된 닉네임입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> USER_NOT_FOUND =
            new ResponseEntity<>("아이디 또는 비밀번호를 확인해주세요.", HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> NOT_LOGIN_EXCEPTION =
            new ResponseEntity<>("로그인 후 이용해주세요.", HttpStatus.UNAUTHORIZED);
}