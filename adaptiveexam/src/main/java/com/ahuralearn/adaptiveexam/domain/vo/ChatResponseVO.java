package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

@Data
public class ChatResponseVO {
    private String replyText;   // AI 生成的解释文本
    private String suggestedLink; // AI 推荐的复习资料链接 (可选)
}