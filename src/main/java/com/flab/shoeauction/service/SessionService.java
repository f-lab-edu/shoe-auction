package com.flab.shoeauction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.flab.shoeauction.util.session.SessionConstants.SMS_CERTIFICATION_NUBER;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final HttpSession session;

    public String getSmsCertificationNumber() {
        return (String) session.getAttribute(SMS_CERTIFICATION_NUBER);
    }

    public void saveSmsCertificationNumber(String number) {
        session.setAttribute(SMS_CERTIFICATION_NUBER, number);
    }

    public void removeSmsCertificationNumber() {
        session.removeAttribute(SMS_CERTIFICATION_NUBER);
    }

}