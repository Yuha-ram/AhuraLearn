package com.ahuralearn.common.config.cloud;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ahuralearn.oss")
@Data
public class AliProperties {

    private String defaultAvatar;

    private String endpoint;

    private String bucketName;

    private String accessKeyId;

    private String accessKeySecret;

    private String urlPrefix;

    public String getDeafultAvatar() {
        return this.urlPrefix + this.defaultAvatar;
    }
}
