package com.ahuralearn.file.domain.vo;

import com.ahuralearn.file.enums.FileStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Acknowledgement returned immediately after an upload.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "Upload acknowledgement")
public class UploadVO {

    /** new file id (String so the snowflake id is JS-safe) */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;

    /** original filename */
    private String filename;

    /** size in bytes */
    private Long size;

    /** processing status (Uploaded, or Failed if text extraction failed) */
    private FileStatus status;
}
