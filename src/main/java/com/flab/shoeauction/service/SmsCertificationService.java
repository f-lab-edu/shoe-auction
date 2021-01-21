package com.flab.shoeauction.service;

import com.flab.shoeauction.dao.SmsCertificationDao;
import com.flab.shoeauction.util.coolSms.SmsMessageTemplate;
import com.flab.shoeauction.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

import static com.flab.shoeauction.util.coolSms.coolSmsConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsCertificationService {

    private final SessionService sessionService;

    private final SmsCertificationDao smsCertificationDao;

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

    // sms로 인증번호 발송하고, 발송 정보를 세션에 저장
    public void sendSms(String phone) {
        Message coolsms = new Message(COOLSMS_KEY, COOLSMS_SECRET);
        String randomNumber = makeRandomNumber();
        String content = makeSmsContent(randomNumber);
        HashMap<String, String> params = makeParams(phone, content);

        /*
        * SMS 발송 비용으로 인한 일시 주석 처리
        try {
            JSONObject result = coolsms.send(params);
            if (result.get("success_count").toString().equals("0")) throw new SmsSendFailedException();
        } catch (CoolsmsException exception) {
            exception.printStackTrace();
        }*/

        smsCertificationDao.createSmsCertification(phone, randomNumber);
    }

    // 입력한 인증번호가 발송되었던(세션에 저장된) 인증번호가 동일한지 확인
    public boolean verifySms(UserDto.SmsCertificationRequest requestDto) {
        if (smsCertificationDao.hasKey(requestDto.getPhone()) &&
                smsCertificationDao.getSmsCertification(requestDto.getPhone()).equals(requestDto.getCertificationNumber())) {
            smsCertificationDao.removeSmsCertification(requestDto.getPhone());
            return true;
        }
        return false;
    }
}