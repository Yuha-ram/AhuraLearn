package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExamQuestionVO {

    private String id;

    // multiple-choice / true-false / short-answer
    private String type;

    private Integer difficulty;

    private String topic;

    // 前端写死叫 question
    private String question;

    private List<OptionVO> options;

    // 前端 mock 里存在
    // submit 时用于判卷
    private String correctAnswer;

    @Data
    public static class OptionVO {
        private String id;
        private String text;
    }
}