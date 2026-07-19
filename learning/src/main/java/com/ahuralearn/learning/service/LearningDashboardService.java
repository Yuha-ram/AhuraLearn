package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.vo.LearningDashboardVO;

public interface LearningDashboardService {
    LearningDashboardVO getDashboard(Long userId);
}
