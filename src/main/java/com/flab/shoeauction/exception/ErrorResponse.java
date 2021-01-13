package com.flab.shoeauction.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


/*
* ExceptionHandler를 통해 반환된 에러를 클라이언트가 받았을 때
* 동일한 방식으로 처리할 수 있게 하도록 정의한 Error 클래스
* HTTP 상태코드, 오류 메시지로 구성되어 있다.
*/

@Getter
public class ErrorResponse {

    private HttpStatus status;
    private String message;

    @Builder
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}