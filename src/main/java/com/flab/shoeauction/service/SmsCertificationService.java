package com.flab.shoeauction.service;

import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsCertificationService {

    private final SessionService sessionService;

    // 6자리 난수 생성
    public String makeRandomNumber() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    // sms 보내기 (log 찍는것으로 대체)
    public void sendSms(String phone) {
        String randomNumber = makeRandomNumber();
        sessionService.saveSmsCertificationNumber(randomNumber);
        log.info(phone + "에 인증번호 [" + randomNumber + "] 발송");
    }

    // 입력한 인증번호가 발급되었던(세션에 저장된) 인증번호와 동일한지 체크
    public boolean phoneVerification(UserDto.CertificationRequest requestDto) {
        if (sessionService.getSmsCertificationNumber().equals(requestDto.getCertificationNumber())) {
            sessionService.removeSmsCertificationNumber();
            return true;
        }
        return false;
    }
}