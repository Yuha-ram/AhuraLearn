package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecentAssessmentVO {

    /* 考试记录 ID */
    private String recordId;

    /* 模块 ID */
    private String moduleId;

    /* 考试成绩 */
    private Integer score;

    /* 正确率 */
    private Double accuracy;

    /* 总题数 */
    private Integer totalQuestions;

    /* 正确题数 */
    private Integer correctCount;

    /* 作答时间（秒） */
    private Integer timeTaken;

    /* 考试时间 */
    private LocalDateTime createdAt;
}