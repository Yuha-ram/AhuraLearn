package com.ahuralearn.learning.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseReviewVO {
    private Long id;
    private Long courseId;
    private Long userId;
    private Integer rating;
    private String comment;
    private String username;
    private LocalDateTime createTime;
}
