package com.flab.shoeauction.service;

import com.flab.shoeauction.util.coolSms.SentSmsCertificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.flab.shoeauction.util.session.SessionConstants.SMS_CERTIFICATION_INFO;

@RequiredArgsConstructor
@Service
public class SessionService {
    private final HttpSession session;

    public SentSmsCertificationInfo getSmsCertificationInfo() {
        return (SentSmsCertificationInfo) session.getAttribute(SMS_CERTIFICATION_INFO);
    }

    public void saveSmsCertificationInfo(SentSmsCertificationInfo sentSmsCertificationInfo) {
        session.setAttribute(SMS_CERTIFICATION_INFO, sentSmsCertificationInfo);
    }

    public void removeSmsCertificationInfo() {
        session.removeAttribute(SMS_CERTIFICATION_INFO);
    }
}