package com.ahuralearn.course.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Instructor Info")
public class InstructorVO {

    private String name;

    private String avatar;

    private String bio;

    private BigDecimal rating;

    private Integer studentCount;

    private Integer courseCount;
}
