package com.ahuralearn.course.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Quiz Question")
public class QuizQuestionDTO {
    private Long questionId;
    private String content;
    private Map<String, String> options;
    private String correctAnswer;
    private Integer score;
}