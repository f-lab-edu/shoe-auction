package com.flab.shoeauction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.flab.shoeauction.util.session.SessionConstants.LOGIN_USER_EMAIL;

@RequiredArgsConstructor
@Service
public class SessionService {

    private final HttpSession session;

    public void saveLoginUserEmail(String email) { session.setAttribute(LOGIN_USER_EMAIL, email); }

    public void removeLoginUserEmail() {
        session.removeAttribute(LOGIN_USER_EMAIL);
    }

    public String getLoginUserEmail() {
        return (String) session.getAttribute(LOGIN_USER_EMAIL);
    }
}