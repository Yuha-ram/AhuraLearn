package com.ahuralearn.ai.domain.vo;

import com.ahuralearn.ai.enums.MessageRole;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Chat Message Entity")
public class ChatMessageVO {

    /** used for iteration render on frontend */
    @Schema(description = "Message ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long messageId;

    @Schema(description = "Role: user / assistant")
    private String role;

    @Schema(description = "Content")
    private String content;
}
