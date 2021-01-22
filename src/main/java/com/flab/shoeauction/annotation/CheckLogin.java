package com.flab.shoeauction.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/*
* 해당 어노테이션이 붙은 경우 로그인이 되어있을 때만
* HandlerInterceptor에서
*/
@Retention(RUNTIME)
@Target(METHOD)
public @interface CheckLogin {
}
