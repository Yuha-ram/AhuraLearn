package com.ahuralearn.learning.domain.vo;

import lombok.Data;

@Data
public class LearningPlanVO {
    private Long id;

    private String title;

    private String studyTime;

    private String priority;

    private Boolean completed;

    private String dueText;

    private String subtitle;

    private String note;

    private Boolean aiGenerated;
}
