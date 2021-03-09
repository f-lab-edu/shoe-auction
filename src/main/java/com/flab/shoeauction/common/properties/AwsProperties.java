package com.flab.shoeauction.common.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {

    private final Credentials credentials = new Credentials();
    private final S3 s3 = new S3();
    private final Region region = new Region();

    @Getter
    @Setter
    public static class Credentials {

        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class S3 {

        private String brand;
        private String product;
    }

    @Getter
    @Setter
    public static class Region {

        private String statics;
    }

    public String getAccessKey() {
        return credentials.getAccessKey();
    }

    public String getSecretKey() {
        return credentials.getSecretKey();
    }

    public String getBrnadBucket() {
        return s3.getBrand();
    }

    public String getProductBucket() {
        return s3.getProduct();
    }

    public String getRegionStatic() {
        return region.getStatics();
    }
}