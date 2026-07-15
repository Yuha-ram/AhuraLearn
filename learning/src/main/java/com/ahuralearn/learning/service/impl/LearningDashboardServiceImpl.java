package com.ahuralearn.learning.service.impl;

import com.ahuralearn.learning.domain.vo.DashboardAchievementCountsVO;
import com.ahuralearn.learning.domain.vo.DashboardOverviewVO;
import com.ahuralearn.learning.domain.vo.DashboardProgressVO;
import com.ahuralearn.learning.domain.vo.LearningDashboardVO;
import com.ahuralearn.learning.mapper.LearningDashboardMapper;
import com.ahuralearn.learning.service.LearningDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningDashboardServiceImpl implements LearningDashboardService {
    private static final int IN_PROGRESS = 1;
    private static final int COMPLETED = 2;
    private static final String PROGRESS_MESSAGE =
            "Keep going! Every lesson brings you closer to your goal.";

    private final LearningDashboardMapper dashboardMapper;

    @Override
    public LearningDashboardVO getDashboard(Long userId) {
        int completedCourses = dashboardMapper.countCoursesByStatus(userId, COMPLETED);
        int ongoingCourses = dashboardMapper.countCoursesByStatus(userId, IN_PROGRESS);
        int overallProgress = dashboardMapper.selectOverallProgress(userId);
        DashboardAchievementCountsVO achievementCounts = dashboardMapper.selectAchievementCounts(userId);
        int achievements = achievementCounts == null || achievementCounts.getAchievements() == null
                ? 0 : achievementCounts.getAchievements();
        int certificates = achievementCounts == null || achievementCounts.getCertificates() == null
                ? 0 : achievementCounts.getCertificates();

        DashboardProgressVO progress = new DashboardProgressVO();
        progress.setCompletedPercent(overallProgress);
        progress.setMessage(PROGRESS_MESSAGE);

        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setAchievements(achievements);
        overview.setCertificates(certificates);
        overview.setCompletedCourses(completedCourses);
        overview.setOngoingCourses(ongoingCourses);

        LearningDashboardVO dashboard = new LearningDashboardVO();
        dashboard.setProgress(progress);
        dashboard.setOngoingCourses(dashboardMapper.selectLatestOngoingCourses(userId));
        dashboard.setOverview(overview);
        return dashboard;
    }

}
