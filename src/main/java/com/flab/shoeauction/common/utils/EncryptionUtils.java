package com.flab.shoeauction.common.utils;

import java.security.MessageDigest;

/**
 * MD5 vs SHA256
 * MD5 : 128비트 암호화 해시 함수. 여러가지 결함으로 인해 보안에는 적합하지 않고, MD5의 결함을 이용해 SSL 인증서 변조까지 가능하다.
 * SHA256 : MD5의 결함을 보완하고 단방향 알고리즘으로 복호화가 불가능하기 때문에 보안에 적합하다.
 */

public class EncryptionUtils  {
    public static String encryptSHA256(String s) {
        return encrypt(s, "SHA-256");
    }

    private static String encrypt(String s, String messageDigest) {
        try {
            MessageDigest md = MessageDigest.getInstance(messageDigest);
            byte[] passBytes = s.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; i++) sb.append(Integer.toString((digested[i]&0xff) + 0x100, 16).substring(1));
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }
}
