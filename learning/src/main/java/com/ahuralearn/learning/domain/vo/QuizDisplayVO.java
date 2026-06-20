package com.ahuralearn.learning.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Schema(description = "Quiz Details")
@Accessors(chain = true)
public class QuizDisplayVO {
    // question itself
    private Long questionId; // for submit quiz feature
    private String content;
    private Map<String, String> options;
    private String correctAnswer;
    private Integer score;

    // user's answer detail
    private String userAnswer;
    private Boolean isCorrect;
    private Integer earnedScore;
}