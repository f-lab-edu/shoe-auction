package com.flab.shoeauction.service;

import static com.flab.shoeauction.common.utils.constants.UserConstants.AUTH;
import static com.flab.shoeauction.common.utils.constants.UserConstants.USER_ID;

import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import com.flab.shoeauction.controller.dto.UserDto.UserInfoDto;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
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
        session.setAttribute(USER_ID, email);
        setAuthSession(email);
    }

    public void setAuthSession(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        session.setAttribute(AUTH, user.getEmailVerified());
    }

    public void logout() {
        session.removeAttribute(USER_ID);
        session.removeAttribute(AUTH);
    }

    public String getLoginUser() {
        return (String) session.getAttribute(USER_ID);
    }

    public boolean isEmailAuth() {
        return (Boolean) session.getAttribute(AUTH);
    }

    public UserInfoDto getCurrentUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다.")).toUserInfoDto();
    }
}