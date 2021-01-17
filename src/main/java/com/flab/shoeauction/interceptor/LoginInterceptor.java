package com.flab.shoeauction.interceptor;

import com.flab.shoeauction.user.exception.UnauthenticatedUserException;
import com.flab.shoeauction.user.service.LoginService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

  private final LoginService loginService;

  public List<String> excludeUrl = List.of("/users","/users/login", "/users/certification/**");

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {


    String loginUser = loginService.getUser();

    if(loginUser == null) {
      throw new UnauthenticatedUserException("로그인 후 이용 가능한 서비스 입니다.");
    }

    return true;

  }
}
