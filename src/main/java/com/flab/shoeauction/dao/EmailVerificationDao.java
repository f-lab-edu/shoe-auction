package com.flab.shoeauction.dao;

import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.LIMIT_TIME_EMAIL_VALIDATION;
import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.PREFIX_VERIFICATION;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmailVerificationDao implements EmailCertificationDao {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void createEmail(String email, String token) {
        stringRedisTemplate.opsForValue()
            .set(PREFIX_VERIFICATION + email, token,
                Duration.ofSeconds(LIMIT_TIME_EMAIL_VALIDATION));
    }

    @Override
    public String getEmailCertification(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX_VERIFICATION + email);
    }

    @Override
    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX_VERIFICATION + email);
    }

    @Override
    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX_VERIFICATION + email);
    }
}
