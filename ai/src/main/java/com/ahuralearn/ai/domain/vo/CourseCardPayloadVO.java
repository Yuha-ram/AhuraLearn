package com.ahuralearn.ai.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用于统一课程卡片在 SSE 和历史回放中的数据结构。
 */
@Data
@Schema(description = "Course Card Payload")
public class CourseCardPayloadVO {

    @Schema(description = "Course ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "Course Name")
    private String name;

    @Schema(description = "Course Cover URL")
    private String coverUrl;

    @Schema(description = "Course difficulty level, e.g. beginner / intermediate / advanced")
    private String difficultyLevel;
}
