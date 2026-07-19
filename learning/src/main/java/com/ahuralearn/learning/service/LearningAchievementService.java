package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.vo.AchievementSummaryVO;

public interface LearningAchievementService {
    AchievementSummaryVO getSummary(Long userId);
}
