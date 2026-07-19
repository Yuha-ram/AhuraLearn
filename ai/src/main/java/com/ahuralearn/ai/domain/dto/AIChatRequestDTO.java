package com.ahuralearn.ai.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI chat request")
public class AIChatRequestDTO {

    @NotBlank(message = "Message cannot be empty")
    @Schema(description = "Question for the AI learning assistant",
            example = "Can you help me make a weekly study plan for Java?")
    private String message;
}
