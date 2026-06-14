package com.ahuralearn.course.domain.vo;

import com.ahuralearn.course.enums.DifficultyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Course Basic Info")
public class CourseBasicInfoVO {

    private Long id;

    private String name;

    private String instructorName;

    private String coverUrl;

    private BigDecimal rating;

    private Integer reviewCount;

    private DifficultyLevel difficultyLevel;
}
