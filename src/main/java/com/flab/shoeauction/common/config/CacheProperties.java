package com.flab.shoeauction.common.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/*
 * 트래픽에 따라 캐시 별 만료기간을 쉽게 변경할 수 있도록 properties에서 cache 이름별 만료 기간을 Map에 바인딩한다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redis-cache-constants")
public class CacheProperties {

    private Map<String, Long> ttl;

    @Getter
    @Setter
    public static class CacheNameAndTtl {

        private String name;
        private String ttl;
    }
}