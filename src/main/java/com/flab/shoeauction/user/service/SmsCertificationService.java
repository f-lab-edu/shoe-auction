package com.flab.shoeauction.user.service;

import static com.flab.shoeauction.user.utils.UserConstants.CERTIFICATION_SESSION_KEY;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsCertificationService {

  private final HttpSession session;

  public void setSmsCertificationService(String certificationNumber) {
    session.setAttribute(CERTIFICATION_SESSION_KEY, certificationNumber);
    session.setMaxInactiveInterval(180);
  }

  public String getSmsCertificationService() {
    return (String) session.getAttribute(CERTIFICATION_SESSION_KEY);
  }
}
