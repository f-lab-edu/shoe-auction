package com.flab.shoeauction.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {

    public static final ResponseEntity<Void> OK_RESPONSE =
            ResponseEntity.ok().build();

    public static final ResponseEntity<Void> CREATED_RESPONSE =
            ResponseEntity.status(HttpStatus.CREATED).build();

    public static final ResponseEntity<String> DUPLICATION_EMAIL_RESPONSE =
            new ResponseEntity<>("중복된 이메일입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> DUPLICATION_NICKNAME_RESPONSE =
            new ResponseEntity<>("중복된 닉네임입니다.", HttpStatus.CONFLICT);
}