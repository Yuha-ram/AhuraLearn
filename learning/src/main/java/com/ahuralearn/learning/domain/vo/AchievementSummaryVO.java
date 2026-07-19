package com.ahuralearn.learning.domain.vo;

import lombok.Data;

@Data
public class AchievementSummaryVO {
    private Integer totalAchievements;
    private Integer certificatesEarned;
    private String certificationName;
    private Integer certificationProgress;
}
