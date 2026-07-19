package com.ahuralearn.learning.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class NotificationPageVO {
    private Long total;

    private Long pages;

    private Integer pageNum;

    private Integer pageSize;

    private List<NotificationVO> records;
}
