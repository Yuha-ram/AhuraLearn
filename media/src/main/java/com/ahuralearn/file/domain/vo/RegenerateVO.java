package com.ahuralearn.file.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Acknowledgement returned after regenerating a summary; the UI then re-fetches
 * the full summary via GET.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@Data
@AllArgsConstructor
@Schema(description = "Summary regeneration acknowledgement")
public class RegenerateVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;

    /** human-readable result message */
    private String message;
}
