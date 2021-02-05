package com.flab.shoeauction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    /*
     * 자바에서 레디스를 사용하기 위해서 레디스 클라이언트가 필요하다.
     * Spring Data Redis에서는 Jedis와 Lecttuce를 지원한다.
     * netty 위에서 구축되어 비동기로 요청을 처리하므로 성능에 장점이 있는 Lecttuce를 채택하였다.
     * 또한 Document가 잘 만들어져 있고, 디자인 코드 또한 깔끔하다.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, redisPort);

        return lettuceConnectionFactory;
    }

    /*
     * RedisTemplate는 커넥션 위에서 레디스 커맨드를 도와준다.
     * 레디스의 데이터 저장방식은 byte[]이기 때문에 값을 저장하고 가져오기 위해서는 직렬화가 필요하다.
     * RedisTemplate 클래스는 default Serializer가 JdkSerializationRedisSerializer이기 때문에
     * 문자열 저장의 특화된 RedisTemplate의 서브 클래스 StringRedisTemplate를 사용했다.
     * StringRedisTemplate의 default Serializer는 StringSerializer이다.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());

        return stringRedisTemplate;
    }
}
