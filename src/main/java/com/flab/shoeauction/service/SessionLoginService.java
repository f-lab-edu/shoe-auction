package com.flab.shoeauction.service;

import static com.flab.shoeauction.common.utils.constants.UserConstants.AUTH_STATUS;
import static com.flab.shoeauction.common.utils.constants.UserConstants.USER_ID;

import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.NotAuthorizedException;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionLoginService {

    private final HttpSession session;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public void existByEmailAndPassword(LoginRequest loginRequest) {
        loginRequest.passwordEncryption(encryptionService);
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (!userRepository.existsByEmailAndPassword(email, password)) {
            throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public void login(LoginRequest loginRequest) {
        existByEmailAndPassword(loginRequest);
        String email = loginRequest.getEmail();
        setUserLevel(email);
        session.setAttribute(USER_ID, email);
    }

    public void setUserLevel(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        banCheck(user);

        session.setAttribute(AUTH_STATUS, user.getUserLevel());
    }

    private void banCheck(User user) {
        if(user.getUserLevel() == UserLevel.BAN) {
            throw new NotAuthorizedException("관리자에 의해 이용이 정지된 사용자 입니다.");
        }
    }

    public UserLevel getUserLevel() {
        return (UserLevel) session.getAttribute(AUTH_STATUS);
    }

    public void logout() {
        session.removeAttribute(USER_ID);
    }

    public String getLoginUser() {
        return (String) session.getAttribute(USER_ID);
    }


    public UserInfoDto getCurrentUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다.")).toUserInfoDto();
    }
}