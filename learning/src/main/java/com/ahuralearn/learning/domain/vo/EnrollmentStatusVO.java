package com.ahuralearn.learning.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Course Enrollment Status")
public class EnrollmentStatusVO {

    private Boolean enrolled;

    @JsonSerialize(using = ToStringSerializer.class) // Avoid loss of precision in JS Number
    private Long latestSectionId;
}
