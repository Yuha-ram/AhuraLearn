package com.ahuralearn.ai.domain.vo;

import com.ahuralearn.ai.enums.MessageRole;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "Chat Message Entity")
@Accessors(chain = true)
public class ChatMessageVO {

    /** used for iteration render on frontend */
    @Schema(description = "Message ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

    @Schema(description = "Role: user / assistant")
    private String role;

    // 修改：返回给前端时显式告知消息渲染类型。
    @Schema(description = "Message Type")
    private String messageType;

    @Schema(description = "Content")
    private String content;

    // 修改：非文本消息通过 payload 返回完整渲染数据。
    @Schema(description = "Payload")
    private Object payload;
}
