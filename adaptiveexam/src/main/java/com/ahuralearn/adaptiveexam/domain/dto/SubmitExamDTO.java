package com.ahuralearn.adaptiveexam.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SubmitExamDTO {

    // 前端传来的答案
    // q1 -> third
    // q2 -> first
    private String moduleId;

    private Long courseId;

    private Map<String, String> answers;

    private Map<String, String> shortAnswers;

    private TimeStats timeStats;

    @Data
    public static class TimeStats {

        private Integer totalTimeSeconds;

        private Map<String, Integer>
                questionTimes;
    }
}