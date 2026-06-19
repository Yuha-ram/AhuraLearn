package com.ahuralearn.media.service;

import java.util.Date;

public interface IMediaService {

    String generateSignedUrl(String objectKey, Date expiration);
}
