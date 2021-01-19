package com.flab.shoeauction.util.coolSms;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SentSmsCertificationInfo {
    private String certificationNumber;
    private LocalDateTime sentTime;

    public SentSmsCertificationInfo(String certificationNumber) {
        this.certificationNumber = certificationNumber;
        this.sentTime = LocalDateTime.now();
    }
}
