package com.flab.shoeauction.common.configs;

import com.flab.shoeauction.common.annotation.LoginUser;
import com.flab.shoeauction.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * HandlerMethodArgumentResolver : 컨트롤러에 사용자의 요청이 도달하기 전에 요청에 대한 파라미터를 수정해서 넘길 수 있는 기능을 제공하는 인터페이스
 * supportsParameter() : 해당 요청이 Resolver가 적용이 가능한지 확인한다.
 * resolveArgument() : supportsParameter() 메소드가 true라면 실제로 바인딩할 객체를 리턴한다. (여기서는 현재 로그인된 User의 정보를 리턴한다.)
 */

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final LoginService loginService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return loginService.getLoginUser();
    }
}
