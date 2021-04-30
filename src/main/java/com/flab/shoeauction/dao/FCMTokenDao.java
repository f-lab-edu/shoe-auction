package com.flab.shoeauction.dao;

import com.flab.shoeauction.controller.dto.UserDto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FCMTokenDao {

    private final StringRedisTemplate tokenRedisTemplate;

    public void saveToken(LoginRequest loginRequest) {
        tokenRedisTemplate.opsForValue()
            .set(loginRequest.getEmail(), loginRequest.getToken());
    }

    public String getToken(String email) {
        return tokenRedisTemplate.opsForValue().get(email);
    }

    public void deleteToken(String email) {
        tokenRedisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return tokenRedisTemplate.hasKey(email);
    }
}