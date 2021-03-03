package com.flab.shoeauction.service.encrytion;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class SHA256EncryptionService implements EncryptionService {

    public String encrypt(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] passBytes = s.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digested.length; i++) {
                sb.append(Integer.toString((digested[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            return s;
        }
    }
}