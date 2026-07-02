package com.ahuralearn.course.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Course Recommendation Metadata")
public class CourseVectorDTO {
    private Long id;
    private String name;
    private String subtitle;
    private String description;
    private Integer difficultyLevel;
    private Integer hoursRequired;
    private String outcomes;
    private String categoryName;
}
