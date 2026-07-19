package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.po.LearningPlan;

public interface NotificationGenerateService {
    void syncLearningPlanDueNotification(LearningPlan plan);

    void deleteLearningPlanDueNotification(Long userId, Long planId);

    void syncWeeklyGoalDueNotification(Long userId, Long goalId, String title, Boolean achieved, String dueDay);

    void deleteWeeklyGoalDueNotification(Long userId, Long goalId);
}
