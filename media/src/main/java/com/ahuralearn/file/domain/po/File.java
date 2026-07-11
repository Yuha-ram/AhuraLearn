package com.ahuralearn.file.domain.po;

import com.ahuralearn.file.enums.FileStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Uploaded file table
 * </p>
 * Persistent entity for an uploaded study material, mapped to the {@code file}
 * table. The binary itself lives in OSS (referenced by {@link #fileUrl}); this
 * row stores only the metadata plus the extracted text used by the UI.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key — snowflake id assigned by MyBatis-Plus (ASSIGN_ID)
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * id of the user who owns this file — stamped from the JWT (UserContext) on
     * upload; every read/delete is scoped to it so users never see each other's files
     */
    private Long userId;

    /**
     * original filename uploaded by the user
     */
    private String originalName;

    /**
     * MIME type, e.g. application/pdf
     */
    private String contentType;

    /**
     * file size in bytes
     */
    private Long fileSize;

    /**
     * public URL of the stored binary in OSS
     */
    private String fileUrl;

    /**
     * lifecycle status (stored as int, see {@link FileStatus}):
     * 1-Uploaded, 4-Failed
     */
    private FileStatus status;

    /**
     * full extracted plain text; the summary + key points are derived from this on read
     */
    private String extractedText;

    /**
     * create time — filled by the DB default (CURRENT_TIMESTAMP)
     */
    private LocalDateTime createTime;

    /**
     * update time — refreshed by the DB on update (ON UPDATE CURRENT_TIMESTAMP)
     */
    private LocalDateTime updateTime;

}
