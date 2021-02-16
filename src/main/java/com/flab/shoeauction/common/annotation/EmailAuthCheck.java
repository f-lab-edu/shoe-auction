package com.flab.shoeauction.common.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @EmailAuthCheck : 현재 사용자가 이메일 인증을 완료한 사용자인지 확인
 * @Retention : 어느 시점까지 어노테이션의 메모리를 가져갈 지 설정
 * @Target : 어노테이션이 사용될 위치를 지정한다.
 */

@Retention(RUNTIME)
@Target(METHOD)
public @interface EmailAuthCheck {

}
