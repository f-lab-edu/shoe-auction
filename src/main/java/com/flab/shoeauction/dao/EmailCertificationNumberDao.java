package com.flab.shoeauction.dao;

import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.LIMIT_TIME_CERTIFICATION_NUMBER;
import static com.flab.shoeauction.common.utils.certification.email.EmailConstants.PREFIX_CERTIFICATION;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmailCertificationNumberDao implements EmailCertificationDao {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void createEmail(String email, String certificationNumber) {
        stringRedisTemplate.opsForValue()
            .set(PREFIX_CERTIFICATION + email, certificationNumber,
                Duration.ofSeconds(LIMIT_TIME_CERTIFICATION_NUMBER));
    }

    @Override
    public String getEmailCertification(String email) {
        return stringRedisTemplate.opsForValue().get(PREFIX_CERTIFICATION + email);
    }

    @Override
    public void removeEmailCertification(String email) {
        stringRedisTemplate.delete(PREFIX_CERTIFICATION + email);
    }

    @Override
    public boolean hasKey(String email) {
        return stringRedisTemplate.hasKey(PREFIX_CERTIFICATION + email);
    }
}

