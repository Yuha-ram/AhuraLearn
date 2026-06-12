package com.ahuralearn.common.config.cloud;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliConfig {

    private final AliProperties aliProperties;

    public AliConfig(AliProperties aliProperties) {
        this.aliProperties = aliProperties;
    }

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                aliProperties.getEndpoint(),
                aliProperties.getAccessKeyId(),
                aliProperties.getAccessKeySecret()
        );
    }
}