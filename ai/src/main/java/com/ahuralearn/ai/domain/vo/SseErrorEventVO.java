package com.ahuralearn.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于统一 SSE 错误事件的输出结构。
 */
@Data
@AllArgsConstructor
@Schema(description = "SSE Error Event")
public class SseErrorEventVO {

    @Schema(description = "Error Code")
    private String code;

    @Schema(description = "Error Message")
    private String message;

    @Schema(description = "Whether the request can be retried")
    private boolean retryable;
}
