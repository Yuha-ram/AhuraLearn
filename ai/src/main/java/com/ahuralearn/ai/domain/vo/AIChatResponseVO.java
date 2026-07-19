package com.ahuralearn.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "AI chat response")
public class AIChatResponseVO {

    @Schema(description = "Message author", example = "assistant")
    private String role;

    @Schema(description = "Answer from the AI learning assistant")
    private String message;
}
