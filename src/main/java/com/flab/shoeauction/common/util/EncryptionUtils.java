package com.flab.shoeauction.common.util;

import java.security.MessageDigest;

public class EncryptionUtils {
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
            for (int i = 0; i < digested.length; i++)
                sb.append(Integer.toString((digested[i] & 0xff) + 0x100, 16).substring(1));
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }
}