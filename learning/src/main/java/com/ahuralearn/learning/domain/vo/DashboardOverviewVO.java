package com.ahuralearn.learning.domain.vo;

import lombok.Data;

@Data
public class DashboardOverviewVO {
    private Integer achievements;
    private Integer certificates;
    private Integer completedCourses;
    private Integer ongoingCourses;
}
