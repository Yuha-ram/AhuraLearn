package com.ahuralearn.game.domain.vo;

import lombok.Data;
import java.util.List;

@Data
public class ChallengeQuestionVO {
    private Long id;
    private String question;
    private List<String> options;
    private String answer;
    private String explanation;
}