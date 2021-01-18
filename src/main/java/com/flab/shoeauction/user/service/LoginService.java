package com.flab.shoeauction.user.service;

import static com.flab.shoeauction.user.utils.UserConstants.USER_ID;

import com.flab.shoeauction.user.exception.UserNotFoundException;
import com.flab.shoeauction.user.repository.UserRepository;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final HttpSession session;

    private final UserRepository userRepository;

    public void existByEmailAndPassword(String email, String password) {
        if (!userRepository.existsByEmailAndPassword(email, password)) {
            throw new UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    public void login(String email) {
        session.setAttribute(USER_ID, email);
    }

    public void logout() {
        session.removeAttribute(USER_ID);
    }

    public String getUser() {
        return (String) session.getAttribute(USER_ID);
    }

}
