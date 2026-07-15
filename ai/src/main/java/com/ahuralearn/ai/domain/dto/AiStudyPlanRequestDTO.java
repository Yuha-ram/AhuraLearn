package com.ahuralearn.ai.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI study plan generation request")
public class AiStudyPlanRequestDTO {

    @NotBlank
    @Schema(description = "Learning goal", example = "Pass the Java certification exam")
    private String goal;

    @NotBlank
    @Schema(description = "Current knowledge level", example = "Beginner")
    private String level;

    @NotBlank
    @Schema(description = "Time available for studying", example = "2 hours per day")
    private String availableTime;

    @NotBlank
    @Schema(description = "Current weak areas", example = "Concurrency and JVM fundamentals")
    private String weakness;
}
