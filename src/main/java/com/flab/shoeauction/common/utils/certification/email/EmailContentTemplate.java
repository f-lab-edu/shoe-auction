package com.flab.shoeauction.common.utils.certification.email;

public class EmailContentTemplate {

    public String getCertificationContent(String certificationNumber) {
        return String.format("%s%s%s", "[Shoe-Auction] 인증번호는 ",certificationNumber,"입니다.");
    }

}
