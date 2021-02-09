package com.flab.shoeauction.common.utils.certification.email;

public class EmailContentTemplate {

    public String buildCertificationContent(String certificationNumber) {

        StringBuilder builder = new StringBuilder();
        builder.append("[Shoe-Auction] 인증번호는 ");
        builder.append(certificationNumber);
        builder.append("입니다. ");

        return builder.toString();
    }

}
