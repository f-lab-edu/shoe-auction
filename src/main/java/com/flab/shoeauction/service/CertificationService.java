package com.flab.shoeauction.service;

import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;

/*
 * SMS 인증 API 사용하려면
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CertificationService {

    private final HttpSession session;

    // 6자리 난수 생성
    public String makeRandomNumber() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    // sms 보내기 (log 찍는것으로 대체)
    public void sendSms(String phone) {
        String randomNumber = makeRandomNumber();
        session.setAttribute(phone, randomNumber);
        log.info("인증번호: " + randomNumber);
    }

    // 인증번호가 세션에 발급된 인증번호와 동일한지 체크
    public boolean phoneVerification(UserDto.CertificationRequest requestDto) {
        if (session.getAttribute(requestDto.getPhone()).equals(requestDto.getCertificationNumber())) {
            session.removeAttribute(requestDto.getPhone());
            return true;
        }
        return false;
    }
}
