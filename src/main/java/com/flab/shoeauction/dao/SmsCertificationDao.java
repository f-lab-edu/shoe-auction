package com.flab.shoeauction.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class SmsCertificationDao {

    private final String PRE_FIX = "sms:";
    private final int LIMIT_TIME = 3 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    public void createSmsCertification(String phone, String certificationNumber) {
        stringRedisTemplate.opsForValue().set(PRE_FIX + phone, certificationNumber);
        stringRedisTemplate.expire(PRE_FIX + phone, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String phone) {
        return stringRedisTemplate.opsForValue().get(PRE_FIX + phone);
    }

    public void removeSmsCertification(String phone) {
        stringRedisTemplate.delete(PRE_FIX + phone);
    }

    public boolean hasKey(String phone) {
        return stringRedisTemplate.hasKey(PRE_FIX + phone);
    }
}