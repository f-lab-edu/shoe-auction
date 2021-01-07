package com.flab.soft.shoeauction.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Component
@RequiredArgsConstructor
public class SessionUtils {
    private static final String CERTIFICATION_SESSION_KEY = "certificationNumber";

    private final HttpSession session;

    public void setCertificationSession(String certificationNumber) {
        session.setAttribute(CERTIFICATION_SESSION_KEY, certificationNumber);
    }

    public String getCertificationSession() {
        return (String) session.getAttribute(CERTIFICATION_SESSION_KEY);
    }

    public void removeCertificationSession(){
        session.removeAttribute(CERTIFICATION_SESSION_KEY);
    }


}
