package com.flab.shoeauction.common.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {

    public static final ResponseEntity<Void> OK =
            ResponseEntity.ok().build();

    public static final ResponseEntity<Void> CREATED =
            ResponseEntity.status(HttpStatus.CREATED).build();

    public static final ResponseEntity<Void> BAD_REQUEST =
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    public static final ResponseEntity<String> DUPLICATION_EMAIL =
            new ResponseEntity<>("중복된 이메일입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> DUPLICATION_NICKNAME =
            new ResponseEntity<>("중복된 닉네임입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> UNAUTHORIZED =
            new ResponseEntity<>(
                    "ID 또는 PW를 확인하세요.", HttpStatus.UNAUTHORIZED
            );

    public static final ResponseEntity<String> LOGIN_UNAUTHORIZED =
            new ResponseEntity<>(
                    "Unauthenticated user", HttpStatus.UNAUTHORIZED
            );

    public static final ResponseEntity<String> WRONG_PASSWORD =
            new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED);
}