package com.ahuralearn.course.domain.vo;

import com.ahuralearn.course.enums.DifficultyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Course Detailed Info")
public class CourseFullInfoVO {

    private Long id;

    private String name;

    private String subtitle;

    private String description; //About this course

    private String categoryName;

    private InstructorVO instructor;

    private BigDecimal rating;

    private Integer reviewCount;

    private DifficultyLevel difficultyLevel;

    private Integer hoursRequired;

    private Integer enrolledCount;

    private String outcomes; //What you'll learn
}
