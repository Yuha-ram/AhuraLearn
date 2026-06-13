package com.ahuralearn.cms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BannerVO {

    private String title;

    private String imageUrl;

    private String targetUrl;
}
