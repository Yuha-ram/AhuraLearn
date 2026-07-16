package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

@Data
public class CourseListVO {

    // 数据库字段
    private String id;
    private String name;

    // React Select
    private String value;
    private String label;
}