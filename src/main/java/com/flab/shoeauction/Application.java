package com.flab.shoeauction;

import com.flab.shoeauction.common.properties.AppProperties;
import com.flab.shoeauction.common.properties.AwsProperties;
import com.flab.shoeauction.common.properties.CacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppProperties.class, CacheProperties.class,
    AwsProperties.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}