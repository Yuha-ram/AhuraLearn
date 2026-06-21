package com.ahuralearn.learning.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Specific Learning Progress")
public class CourseLearningProgressVO {

    @Schema(description = "Set of section ids that have been learned")
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private Set<Long> completedSectionIds;

    @Schema(description = "The current section's historical progress")
    private Integer moment; // used for Resume playback
}
