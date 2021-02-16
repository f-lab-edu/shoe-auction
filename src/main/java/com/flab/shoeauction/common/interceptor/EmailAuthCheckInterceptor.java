package com.flab.shoeauction.common.interceptor;

import com.flab.shoeauction.common.annotation.EmailAuthCheck;
import com.flab.shoeauction.domain.user.User;
import com.flab.shoeauction.domain.user.UserRepository;
import com.flab.shoeauction.exception.user.UnauthenticatedUserException;
import com.flab.shoeauction.service.LoginService;
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
public class EmailAuthCheckInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler)
        throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            EmailAuthCheck emailAuthCheck = handlerMethod.getMethodAnnotation(EmailAuthCheck.class);

            if (emailAuthCheck == null) {
                return true;
            }

            String loginUser = loginService.getLoginUser();
            User user = userRepository.findByEmail(loginUser)
                .orElseThrow(() -> new UnauthenticatedUserException("로그인 후 이용 가능합니다."));

            if(!user.getEmailVerified()) {
                throw new UnauthenticatedUserException("해당 서비스는 이메일 인증 후 이용 가능합니다.");
            }
        }
        return true;
    }
}