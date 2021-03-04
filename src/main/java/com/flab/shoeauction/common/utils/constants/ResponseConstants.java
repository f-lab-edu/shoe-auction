package com.flab.shoeauction.common.utils.constants;

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

    public static final ResponseEntity<String> USER_NOT_FOUND =
        new ResponseEntity<>(
            "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST
        );

    public static final ResponseEntity<String> UNAUTHORIZED_USER =
        new ResponseEntity<>(
            "Unauthenticated user", HttpStatus.UNAUTHORIZED
        );

    public static final ResponseEntity FAIL_TO_CHANGE_NICKNAME =
        new ResponseEntity<>(
            "닉네임은 7일에 한번만 변경이 가능합니다.", HttpStatus.BAD_REQUEST
        );

    public static final ResponseEntity<String> WRONG_PASSWORD =
        new ResponseEntity<>("비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> DUPLICATION_BRAND_NAME =
        new ResponseEntity<>("이미 존재하는 브랜드명입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> BRAND_NOT_FOUND =
        new ResponseEntity<>("존재하지 않는 브랜드입니다.", HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> DUPLICATION_MODEL_NUMBER =
        new ResponseEntity<>("이미 존재하는 모델 넘버입니다.", HttpStatus.CONFLICT);

    public static final ResponseEntity<String> PRODUCT_NOT_FOUND =
        new ResponseEntity<>("존재하지 않는 상품입니다.", HttpStatus.BAD_REQUEST);

    public static final ResponseEntity<String> TOKEN_EXPIRED =
        new ResponseEntity<>("인증 토큰이 만료되었습니다. 마이페이지에서 인증 토큰 재전송 버튼을 클릭해 주세요!",
            HttpStatus.UNAUTHORIZED);

    public static final ResponseEntity<String> NOT_AUTHORIZED =
        new ResponseEntity<>("해당 리소스에 대한 접근 권한이 없습니다.",
            HttpStatus.FORBIDDEN);

    public static final ResponseEntity<String> ILLEGAL_MIME_TYPE =
        new ResponseEntity<>("첨부파일의 확장자가 올바르지 않습니다.",
            HttpStatus.UNSUPPORTED_MEDIA_TYPE);

    public static final ResponseEntity<String> IMAGE_ROAD_FAILED =
        new ResponseEntity<>("이미지 로드에 실패하였습니다.",
            HttpStatus.BAD_REQUEST);
}
