package com.flab.shoeauction.common.utils.certification.email;

public class EmailContentTemplate {

    public String buildCertificationContent(String certificationNumber) {

        StringBuilder builder = new StringBuilder();
        builder.append("[Shoe-Auction] 인증번호는 ");
        builder.append(certificationNumber);
        builder.append("입니다. ");

        return builder.toString();
    }

    public String buildEmailCheckContent(String token, String email) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://localhost:8080/users/email-check-token?token=");
        builder.append(token);
        builder.append("&email=");
        builder.append(email);
        return builder.toString();
    }

}
