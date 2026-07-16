package com.ahuralearn.assistant.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for POST /api/assistant/chat.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@Schema(description = "Academic assistant request")
public class ChatDTO {

    /** optional id of the document to ground the answer on (null = none selected) */
    @Schema(description = "optional document id to ground the answer on")
    private Long documentId;

    /** the user's question (required) */
    @NotBlank(message = "message is required")
    @Schema(description = "user question")
    private String message;
}
