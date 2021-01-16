package com.flab.shoeauction.user.service;

import static com.flab.shoeauction.user.utils.UserConstants.CERTIFICATION_SESSION_KEY;
import static com.flab.shoeauction.user.utils.UserConstants.NUMBER_GENERATION_COUNT;

import java.util.HashMap;
import java.util.Random;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsCertificationService {

  private final HttpSession session;

  public void sendCertificationNumber(String phoneNumber) {
    String certificationNumber = createdRandomNumber();
    setCertificationSession(CERTIFICATION_SESSION_KEY, certificationNumber);
    sendMessage(phoneNumber,certificationNumber);
  }

  public String getCertificationSession() {
    return (String) session.getAttribute(CERTIFICATION_SESSION_KEY);
  }


  private void setCertificationSession(String key, String certificationNumber){
    session.setAttribute(CERTIFICATION_SESSION_KEY, certificationNumber);
    session.setMaxInactiveInterval(180);
  }


  private void sendMessage(String phoneNumber, String certificationNumber) {

    String api_key = "NCSBVON9BZGTHH2U";
    String api_secret = "CLFRQNF13AWV5VHXSMTFWZKIZRJISLK7";
    Message coolsms = new Message(api_key, api_secret);

    // 4 params(to, from, type, text) are mandatory. must be filled
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("to", phoneNumber);    // 수신전화번호
    params.put("from", "01020180103");    // 발신전화번호
    params.put("type", "SMS");
    params.put("text", "shoe-action 휴대폰인증  : 인증번호는" + "["+certificationNumber+"]" + "입니다.");
    params.put("app_version", "test app 1.2"); // application name and version

    try {
      JSONObject obj = (JSONObject) coolsms.send(params);
      System.out.println(obj.toString());
    } catch (CoolsmsException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getCode());
    }

  }

  private String createdRandomNumber() {
    Random rand = new Random();
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < NUMBER_GENERATION_COUNT; i++) {
      stringBuilder.append((rand.nextInt(10)));
    }
    return stringBuilder.toString();
  }

  public boolean certificationNumberInspection(String certificationNumber) {
    return getCertificationSession().equals(certificationNumber);
  }

}