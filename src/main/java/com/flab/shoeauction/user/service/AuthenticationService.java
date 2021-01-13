package com.flab.shoeauction.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpSession;
import static com.flab.shoeauction.user.utils.UserConstants.CERTIFICATION_SESSION_KEY;

@Component
@RequiredArgsConstructor
public class AuthenticationService {

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
