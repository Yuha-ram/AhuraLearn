package com.ahuralearn.ai.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Chat Session Entity")
public class ChatSessionVO {

    @Schema(description = "Session ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sessionId;

    @Schema(description = "Session Name")
    private String title;

    // 修改：会话列表增加状态字段，便于前端感知失败会话。
    @Schema(description = "Session Status")
    private String status;
}
