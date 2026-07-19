package com.ahuralearn.learning.domain.vo;

import lombok.Data;

@Data
public class DashboardProgressVO {
    private Integer completedPercent;
    private String message;
}
