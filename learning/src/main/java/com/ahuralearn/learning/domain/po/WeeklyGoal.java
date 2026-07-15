package com.ahuralearn.learning.domain.po;

import lombok.Data;

@Data
public class WeeklyGoal {
    private Long id;
    private Long userId;
    private String title;
    private String type;
    private Integer currentValue;
    private Integer totalValue;
    private Boolean achieved;
    private String achievedDay;
    private String dueDay;
}
