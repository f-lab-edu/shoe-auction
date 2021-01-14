package com.flab.shoeauction.user.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CERTIFICATION_SESSION_KEY = "certificationNumber";
    private static final String USER_ID = "email";
    private final HttpSession session;

    public void setCertificationSession(String certificationNumber) {
        session.setAttribute(CERTIFICATION_SESSION_KEY, certificationNumber);
    }

    public String getCertificationSession() {
        return (String) session.getAttribute(CERTIFICATION_SESSION_KEY);
    }

    public void removeCertificationSession() {
        session.removeAttribute(CERTIFICATION_SESSION_KEY);
    }

    public void setLoginSession(String email) {
        session.setAttribute(USER_ID, email);
    }

    public String getLoginSession() {
        return (String) session.getAttribute(USER_ID);
    }

    public void removeLoginSession() {
        session.removeAttribute(USER_ID);

    }


}
