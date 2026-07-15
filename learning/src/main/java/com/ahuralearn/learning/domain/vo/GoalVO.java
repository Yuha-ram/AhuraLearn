package com.ahuralearn.learning.domain.vo;

import lombok.Data;

@Data
public class GoalVO {
    private Long id;
    private String title;
    private String type;
    private Integer currentValue;
    private Integer totalValue;
    private Boolean achieved;
    private String achievedDay;
    private String dueDay;
}
