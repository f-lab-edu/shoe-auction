package com.flab.shoeauction.common.utils.certification.email;

public class EmailContentTemplate {

    private final String prefix = "[Shoe-Auction] 인증번호는 ";
    private final String suffix = "입니다.";

    public String parse(String certificationNumber) {
        return String.format("%s%s%s", prefix,certificationNumber,suffix);
    }

}
