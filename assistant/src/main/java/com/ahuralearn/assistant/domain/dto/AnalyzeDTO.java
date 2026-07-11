package com.ahuralearn.assistant.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for POST /api/assistant/analyze.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@Schema(description = "Academic analysis request")
public class AnalyzeDTO {

    /** the free-form question to analyze against all study materials (required) */
    @NotBlank(message = "query is required")
    @Schema(description = "free-form question to analyze against the study materials")
    private String query;

    /**
     * When true, return just the model's answer as plain text (no definition / key-points
     * breakdown and no source lookup). Used by tools like the Citation Generator, whose
     * output is a formatted block, not a research analysis.
     */
    @Schema(description = "return the answer as plain text without the analysis structure/sources")
    private boolean plain;
}
