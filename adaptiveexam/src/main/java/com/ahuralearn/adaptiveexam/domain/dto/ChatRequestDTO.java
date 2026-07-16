package com.ahuralearn.adaptiveexam.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequestDTO {
    @NotBlank(message = "题目ID不能为空，AI需要知道你在问哪道题")
    private String questionId;

    @NotBlank(message = "提问内容不能为空")
    private String userMessage; // 用户的提问，例如："我还是不懂为什么选 B？"
}