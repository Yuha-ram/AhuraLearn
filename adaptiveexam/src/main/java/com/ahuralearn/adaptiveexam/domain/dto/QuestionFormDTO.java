package com.ahuralearn.adaptiveexam.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionFormDTO {
    private String moduleId;
    private String questionText;
    private List<String> options; // 前端传过来的是 List 数组
    private String correctAnswer;
}