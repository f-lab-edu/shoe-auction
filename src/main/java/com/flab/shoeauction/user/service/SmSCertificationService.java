package com.flab.shoeauction.user.service;

import static com.flab.shoeauction.user.utils.UserConstants.API_KEY;
import static com.flab.shoeauction.user.utils.UserConstants.API_SECRET;
import static com.flab.shoeauction.user.utils.UserConstants.NUMBER_GENERATION_COUNT;

import com.flab.shoeauction.user.exception.FailedToSendMessageException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmSCertificationService {

    private final ConcurrentHashMap<String, String> certificationInformation = new ConcurrentHashMap<>();

    public void setCertificationInformation(String phoneNumber){
        String randomNumber = createdRandomNumber();
        certificationInformation.put(phoneNumber, randomNumber);
        sendMessage(phoneNumber, randomNumber);
    }

    public boolean certificationNumberInspection(String certificationNumber, String phoneNumber) {
        return certificationInformation.get(phoneNumber).equals(certificationNumber);
    }


    public String createdRandomNumber() {
        Random rand = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < NUMBER_GENERATION_COUNT; i++) {
            stringBuilder.append((rand.nextInt(10)));
        }
        return stringBuilder.toString();
    }

    private void sendMessage(String phoneNumber, String certificationNumber) {

        Message coolsms = new Message(API_KEY, API_SECRET);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);    // 수신전화번호
        params.put("from", "01020180103");    // 발신전화번호
        params.put("type", "SMS");
        params.put("text", "shoe-action 휴대폰인증  : 인증번호는" + "[" + certificationNumber + "]" + "입니다.");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            coolsms.send(params);
        } catch (CoolsmsException e) {
            log.error("Message transmission failure : error code{}", e.getCode());
            throw new FailedToSendMessageException();
        }
    }
}