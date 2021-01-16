package com.flab.shoeauction.service;

import com.flab.shoeauction.exception.smsCetification.SmsSendFailedException;
import com.flab.shoeauction.util.coolSms.SmsMessageTemplate;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

import static com.flab.shoeauction.util.coolSms.coolSmsKeyConstants.*;

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

    // 인증 메세지 내용 생성
    public String makeSmsContent(String certificationNumber) {
        SmsMessageTemplate content = new SmsMessageTemplate();
        content.setcertificationNumber(certificationNumber);
        return content.parse();
    }

    public HashMap<String, String> makeParams(String to, String text) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", OFFICIAL_PHONE);
        params.put("type", SMS_TYPE);
        params.put("app_version", APP_VERSION);
        params.put("to", to);
        params.put("text", text);
        return params;
    }

    // sms 보내기
    public void sendSms(String phone) {
        Message coolsms = new Message(COOLSMS_KEY, COOLSMS_SECRET);
        String randomNumber = makeRandomNumber();
        String content = makeSmsContent(randomNumber);

        HashMap<String, String> params = makeParams(phone, content);

        try {
            JSONObject result = coolsms.send(params);
            if (result.get("success_count").toString().equals("0")) throw new SmsSendFailedException();
        } catch (CoolsmsException exception) {
            exception.printStackTrace();
        }

        sessionService.saveSmsCertificationNumber(randomNumber);
    }

    // 입력한 인증번호가 발급되었던(세션에 저장된) 인증번호와 동일한지 체크
    public boolean phoneVerification(UserDto.SmsCertificationRequest requestDto) {
        if (sessionService.getSmsCertificationNumber().equals(requestDto.getCertificationNumber())) {
            sessionService.removeSmsCertificationNumber();
            return true;
        }
        return false;
    }
}