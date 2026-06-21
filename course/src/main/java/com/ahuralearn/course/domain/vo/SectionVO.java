package com.ahuralearn.course.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Chapter Section")
public class SectionVO {
    @JsonSerialize(using = ToStringSerializer.class) // Avoid loss of precision in JS Number
    private Long id;
    private String title;
    private Integer duration;
    private String durationFormat; // Format duration (e.g., "12:30")
    private Integer sortOrder;
}
