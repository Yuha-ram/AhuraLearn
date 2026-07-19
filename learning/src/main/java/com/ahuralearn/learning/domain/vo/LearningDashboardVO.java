package com.ahuralearn.learning.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class LearningDashboardVO {
    private DashboardProgressVO progress;
    private List<LearningCourseCardVO> ongoingCourses;
    private DashboardOverviewVO overview;
}
