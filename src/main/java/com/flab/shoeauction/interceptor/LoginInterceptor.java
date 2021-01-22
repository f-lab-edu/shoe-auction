package com.flab.shoeauction.interceptor;

import com.flab.shoeauction.annotation.CheckLogin;
import com.flab.shoeauction.exception.user.NotLoginException;
import com.flab.shoeauction.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CheckLogin checkLogin = ((HandlerMethod) handler).getMethodAnnotation(CheckLogin.class);

        if (checkLogin != null && sessionService.getLoginUserEmail() == null)
            throw new NotLoginException();
        return true;
    }
    
    /*
    * 권한 별 접근 구현 예정 
    */
}