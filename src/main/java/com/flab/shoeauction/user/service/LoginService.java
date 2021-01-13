package com.flab.shoeauction.user.service;

import com.flab.shoeauction.user.exception.UserNotFoundException;
import com.flab.shoeauction.user.repository.UserRepository;
import com.flab.shoeauction.user.utils.AuthenticationSessionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationSessionUtils authenticationSessionUtils;

    public void existByEmailAndPassword(String email, String password) {
        if(!userRepository.existsByEmailAndPassword(email,password)) {
            throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public void login(String email) {
        authenticationSessionUtils.setLoginSession(email);
        log.info(authenticationSessionUtils.getLoginSession());
    }

    public void logout() {
        authenticationSessionUtils.removeCertificationSession();
        log.info("Logout successful");
    }

}
