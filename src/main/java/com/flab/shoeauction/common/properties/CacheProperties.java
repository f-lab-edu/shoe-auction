package com.flab.shoeauction.common.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/*
 * 트래픽에 따라 캐시 별 만료기간을 쉽게 변경할 수 있도록 properties에서 cache 이름별 만료 기간을 Map에 바인딩한다.
 */
@Getter
@ConfigurationProperties(prefix = "cache.redis")
public class CacheProperties {

    private final Map<String, Long> ttl = new HashMap<>();
}