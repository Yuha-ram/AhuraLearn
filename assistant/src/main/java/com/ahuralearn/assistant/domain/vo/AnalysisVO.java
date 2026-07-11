package com.ahuralearn.assistant.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Structured result of an academic analysis. The field shape matches exactly
 * what the frontend expects: inquiry / definition / explanation / keyPoints / sources.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "Academic analysis result")
public class AnalysisVO {

    /** the original question that was asked */
    private String inquiry;

    /** short, direct answer */
    private String definition;

    /** fuller explanation drawn from the documents */
    private String explanation;

    /** supporting bullet points */
    private List<String> keyPoints;

    /** credible external references (title + link) backing the answer */
    private List<SourceVO> sources;
}
