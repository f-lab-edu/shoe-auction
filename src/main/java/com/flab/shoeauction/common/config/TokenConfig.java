package com.flab.shoeauction.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class TokenConfig {

    @Value("${spring.redis.token.host}")
    private String redisHost;

    @Value("${spring.redis.token.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisTokenConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost,
            redisPort);
        return lettuceConnectionFactory;
    }

    @Bean(name = "tokenRedisTemplate")
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisTokenConnectionFactory());
        return stringRedisTemplate;
    }
}
