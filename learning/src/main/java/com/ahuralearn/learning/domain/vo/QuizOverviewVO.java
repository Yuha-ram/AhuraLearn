package com.ahuralearn.learning.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Schema(description = "Quiz Overview")
@AllArgsConstructor
public class QuizOverviewVO {

    @Schema(description = "Whether the user has attempted the quiz", example = "false")
    private Boolean isAttempted;

    @Schema(description = "Actual score earned by the user (null if not attempted)", example = "40")
    private Integer earnedScore;

    @Schema(description = "Total score of the quiz", example = "50")
    private Integer totalScore;

    @Schema(description = "Time of quiz submission")
    private LocalDateTime commitTime;
}