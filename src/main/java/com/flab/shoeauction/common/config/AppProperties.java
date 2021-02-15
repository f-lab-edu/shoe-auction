package com.flab.shoeauction.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("certification-related-constants")
public class AppProperties {
    private String emailFromAddress;
    private String coolSmsKey;
    private String coolSmsSecret;
    private String coolSmsFromPhoneNumber;
}

