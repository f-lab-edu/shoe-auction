package com.flab.shoeauction.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.flab.shoeauction.user.utils.UserConstants.CERTIFICATION_SESSION_KEY;

@Service
@RequiredArgsConstructor
public class SmsCertificationService {

    private final HttpSession session;

    public void setCertificationSession(String certificationNumber) {
        session.setAttribute(CERTIFICATION_SESSION_KEY, certificationNumber);
        session.setMaxInactiveInterval(180);
    }

    public String getCertificationSession() {
        return (String) session.getAttribute(CERTIFICATION_SESSION_KEY);
    }
}
