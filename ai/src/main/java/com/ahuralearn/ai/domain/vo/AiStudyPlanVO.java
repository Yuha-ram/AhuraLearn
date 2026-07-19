package com.ahuralearn.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Generated AI study plan")
public class AiStudyPlanVO {

    @Schema(description = "Study plan content")
    private String plan;
}
