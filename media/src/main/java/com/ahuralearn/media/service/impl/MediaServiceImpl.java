package com.ahuralearn.media.service.impl;

import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.media.config.cloud.AliProperties;
import com.ahuralearn.media.service.IMediaService;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements IMediaService {

    private final OSS ossClient;

    @Value("${ahuralearn.oss.bucket-name}")
    private String bucketName;

    @Override
    public String generateSignedUrl(String objectKey, Date expiration) {
        try {
            if (objectKey == null || objectKey.trim().isEmpty())
                throw new IllegalArgumentException("OSS objectKey cannot be empty");

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey, HttpMethod.GET);
            request.setExpiration(expiration);

            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("Failed to generate Aliyun OSS pre-signed URL, objectKey: {}", objectKey, e);
            throw new BusinessException("Failed to acquire video stream. Please try again later");
        }
    }
}
