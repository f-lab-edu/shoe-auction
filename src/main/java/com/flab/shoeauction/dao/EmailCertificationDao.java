package com.flab.shoeauction.dao;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmailCertificationDao {

    private final String PREFIX = "email:";
    private final int LIMIT_TIME = 10 * 60;

    private final StringRedisTemplate stringRedisTemplate;

    public void createEmailCertification(String email, String certificationNumber) {
        stringRedisTemplate.opsForValue()
            .set(PREFIX + email, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getEmailCertification(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX + email);
    }

    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX + email);
    }

    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX + email);
    }
}

