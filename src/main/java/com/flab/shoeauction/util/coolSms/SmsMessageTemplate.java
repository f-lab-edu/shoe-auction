package com.flab.shoeauction.util.coolSms;

public class SmsMessageTemplate {
    private final String PREFIX = "[Shoe-Auction] 인증번호는 ";
    private final String SUFFIX = "입니다.";
    private String certificationNumber;

    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }

    public String parse() {
        return PREFIX.concat(certificationNumber).concat(SUFFIX);
    }

}