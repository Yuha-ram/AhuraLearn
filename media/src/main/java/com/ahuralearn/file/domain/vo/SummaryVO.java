package com.ahuralearn.file.domain.vo;

import com.ahuralearn.file.enums.FileStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Summary + key points returned to the AI-Summarization view.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "File summary result")
public class SummaryVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;

    /** original filename */
    private String filename;

    /** the AI executive summary */
    private String summary;

    /** key points as a list (split from the stored newline-joined text) */
    private List<String> keyPoints;

    /** current status of the file */
    private FileStatus status;
}
