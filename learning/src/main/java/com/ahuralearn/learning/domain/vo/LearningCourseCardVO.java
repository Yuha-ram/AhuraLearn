package com.ahuralearn.learning.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LearningCourseCardVO {
    private Long id;
    private String title;
    private String subtitle;
    private String instructor;
    private String image;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer learnedSections;
    private Integer totalSections;
    private Integer progress;
    private String status;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long latestSectionId;
}
