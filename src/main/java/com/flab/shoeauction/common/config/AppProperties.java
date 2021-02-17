package com.flab.shoeauction.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("external-certification")
public class AppProperties {
    private final String emailFromAddress;
    private final String coolSmsKey;
    private final String coolSmsSecret;
    private final String coolSmsFromPhoneNumber;
}

