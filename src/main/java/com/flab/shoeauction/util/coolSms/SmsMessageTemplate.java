package com.flab.shoeauction.util.coolSms;

public class SmsMessageTemplate {
    private final String prefix = "[Shoe-Auction] 인증번호는 ";
    private final String suffix = "입니다.";
    private String certificationNumber;

    public void setcertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }

    public String parse() {
        return prefix.concat(certificationNumber).concat(suffix);
    }

}
