package com.ahuralearn.learning.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Learning Record")
public class LearningRecordFormDTO {

    @Schema(description = "The unique identifier of the course", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;

    @Schema(description = "The unique identifier of the specific section", requiredMode = Schema.RequiredMode.REQUIRED, example = "55062")
    @NotNull(message = "Section ID cannot be null")
    private Long sectionId;

    @Schema(description = "Current playback time", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    @NotNull(message = "Playback moment cannot be null")
    private Integer moment;
}