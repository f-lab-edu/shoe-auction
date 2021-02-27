package com.flab.shoeauction.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @EnableCaching :@Cacheable, @CacheEvict 등의 캐시 어노테이션 활성화를 위한 어노테이션이다. AOP 를 이용한 캐싱 기능을 추상화 시켜주므로
 * 별도의 캐시 관련 로직없이 간단하게 적용해 줄 수 있다. 프록시는 Spring AOP 를 이용하여 동작하며 mode 설정을 통해 aspectj 를 이용한 방법으로 설정해 줄
 * 수도 있다.
 * @Cacheable AOP 를 이용해 메소드가 실행되는 시점에 캐시 존재 여부를 검사하고 캐시가 등록되어 있다면 캐시를 등록하고, 캐시가 등록되어 있다면 메소드를 실행시키지
 * 않고 캐싱된 데이터를 return 해준다. value 설정을 통해 캐시명을 지정해 줄 수 있으며, 같은 캐시명을 사용할 때 key 설정을 통해 캐시에 사용될 인자를 선택할 수
 * 있다.
 * @CacheEvict 메소드 실행시 사용하지 않거나 오래된 캐시의 데이터를 삭제할 수 있다. 메소드의 작업 내용으로 인해 영향을 받는 캐시를 지정해서 사용한다. 기본적으로
 * 메소드가 성공적으로 완료되었을 때 캐시를 삭제하지만 beforeInvocation 설정으로 메소드 실행 전 캐시를 삭제할 수도 있다.
 */

@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    CacheProperties cacheProperties;

    private RedisCacheConfiguration redisCacheDefaultConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return redisCacheConfiguration;
    }

    /*
     * properties에서 가져온 캐시명과 ttl 값으로 RedisCacheConfiguration을 만들고 Map에 넣어 반환한다.
     */
    private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        for (Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
            cacheConfigurations
                .put(cacheNameAndTimeout.getKey(), redisCacheDefaultConfiguration().entryTtl(
                    Duration.ofSeconds(cacheNameAndTimeout.getValue())));
        }
        return cacheConfigurations;
    }

    /*
     * 기본적으로 스프링에서 지원하는 캐시 기능의 캐시 저장소는 JDK 의 ConcurrentHashMap 이며,
     * 그 외 캐시 저장소를 사용하기 위해서는 CacheManager Bean 으로 등록하여 사용할 수 있다.
     * 보통 스프링에서 간단하게 사용할 수 있는 Java 기반의 로컬 캐시인 Ehcache 를 많이 사용하지만,
     * 본 어플리케이션은 지속적인 트래픽 증가로 인해 어플리케이션 서버를 점점 확장(스케일 아웃)한다는
     * 가정하에 개발하기 때문에 Redis 를 이용한 글로벌 캐시 적용을 선택했다.
     *
     * 글로벌 캐시 전략은 별도의 캐시 서버를 이용하기 때문에 로컬 캐시 전략보다 캐시 조회는 느리지만
     * 캐시에 저장된 데이터가 변경되는 경우 서버마다 변경 사항을 전달하는 작업이 필요 없기 때문에
     * 서비스 확장으로 WAS 인스턴스가 늘어나고 Cache 데이터가 커질 수록 효과적이다.
     *
     * properties에서 가져온 캐시명과 ttl 값으로 만든 RedisCacheConfiguration Map을
     * withInitialCacheConfigurations에 설정하여서 캐시 별로 만료기간을 다르게 설정하였다.
     */
    @Bean
    public CacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(redisCacheDefaultConfiguration())
            .withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
        return redisCacheManager;
    }
}