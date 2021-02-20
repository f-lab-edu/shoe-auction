package com.flab.shoeauction.common.interceptor;

import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.common.annotation.LoginCheck.EmailAuthStatus;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.service.SessionLoginService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HandlerMethod  : 실행될 핸들러(컨트롤러의 메소드) loginCheck가 null이라면 로그인 없이 접근가능한 핸들러이므로 true 리턴
 */

@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final SessionLoginService sessionLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler)
        throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginCheck loginCheck = handlerMethod.getMethodAnnotation(LoginCheck.class);

            if (loginCheck == null) {
                return true;
            }

            if (sessionLoginService.getLoginUser() == null) {
                throw new UnauthenticatedUserException("로그인 후 이용 가능합니다.");
            }

            EmailAuthStatus authStatus = loginCheck.authority();
            if (authStatus == EmailAuthStatus.AUTH) {
                if (!sessionLoginService.getEmailAuth()) {
                    throw new UnauthenticatedUserException("이메일 인증 후 이용 가능합니다.");
                }

            }
        }
        return true;
    }
}