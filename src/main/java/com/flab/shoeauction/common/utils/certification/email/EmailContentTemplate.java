package com.flab.shoeauction.common.utils.certification.email;

public class EmailContentTemplate {

    public String getCertificationContent(String certificationNumber) {

        StringBuilder bd = new StringBuilder();
        bd.append("[Shoe-Auction] 인증번호는 ");
        bd.append(certificationNumber);
        bd.append("입니다. ");

        return bd.toString();
    }

}
