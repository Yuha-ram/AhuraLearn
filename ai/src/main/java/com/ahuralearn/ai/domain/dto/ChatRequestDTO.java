package com.ahuralearn.ai.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User's Request Entity")
public class ChatRequestDTO {

    /**
     * Session ID (optional)
     * - null -> new chat
     * - not null -> old chat
     */
    @Schema(description = "Session ID")
    private Long sessionId;

    @Schema(description = "User's Question", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
