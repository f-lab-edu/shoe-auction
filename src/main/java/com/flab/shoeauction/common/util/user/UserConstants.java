package com.flab.shoeauction.common.util.user;

import java.util.Random;

public class UserConstants {
    public static final String USER_ID = "email";

    public static final String makeRandomNumber() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }
}