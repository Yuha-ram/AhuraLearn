package com.ahuralearn.file.domain.vo;

import com.ahuralearn.file.enums.FileStatus;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * One row in the file list / detail. Built from
 * {@link com.ahuralearn.file.domain.po.File} via BeanUtils.copyList; the heavy
 * extractedText field is intentionally left out.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "File list/detail item")
public class FileVO {

    /** file id — serialized as a String so the 19-digit snowflake stays JS-safe */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** original filename */
    private String originalName;

    /** MIME type */
    private String contentType;

    /** size in bytes */
    private Long fileSize;

    /** OSS URL of the stored file */
    private String fileUrl;

    /** lifecycle status, serialized as its label (e.g. "Uploaded") */
    private FileStatus status;

    /** AI executive summary */
    private String summary;
}
