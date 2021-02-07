package com.flab.shoeauction.common.utils.certification.coolSms;

public class SmsMessageTemplate {
    public String getCertificationContent(String certificationNumber) {
        return String.format("%s%s%s", "[Shoe-Auction] 인증번호는 ",certificationNumber,"입니다.");
    }

}