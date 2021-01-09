package com.flab.shoeauction.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ExceptionResponse {
    private LocalDateTime timeStamp;
    private String message;
    private String details;
}
