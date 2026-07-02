package com.ahuralearn.ai.domain.dto;

import com.ahuralearn.ai.enums.MessageRole;
import lombok.Data;

@Data
public class ChatMessageBasicDTO {

    private MessageRole role;

    private String content;
}
