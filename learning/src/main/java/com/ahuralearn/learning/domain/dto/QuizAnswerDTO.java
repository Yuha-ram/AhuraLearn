package com.ahuralearn.learning.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "User Answer")
public class QuizAnswerDTO {

    @Schema(description = "question id", example = "101")
    @NotNull(message = "Question Id can't be empty")
    private Long questionId;

    @Schema(description = "user's answer", example = "A")
    @NotBlank(message = "Answer can't be empty")
    private String userAnswer;
}