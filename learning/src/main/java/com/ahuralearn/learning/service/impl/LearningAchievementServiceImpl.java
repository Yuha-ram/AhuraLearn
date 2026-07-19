package com.ahuralearn.learning.service.impl;

import com.ahuralearn.learning.domain.vo.AchievementSummaryVO;
import com.ahuralearn.learning.mapper.LearningAchievementMapper;
import com.ahuralearn.learning.service.LearningAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningAchievementServiceImpl implements LearningAchievementService {
    private final LearningAchievementMapper achievementMapper;

    @Override
    public AchievementSummaryVO getSummary(Long userId) {
        AchievementSummaryVO summary = achievementMapper.selectSummaryByUserId(userId);
        return summary == null ? emptySummary() : summary;
    }

    private AchievementSummaryVO emptySummary() {
        AchievementSummaryVO summary = new AchievementSummaryVO();
        summary.setTotalAchievements(0);
        summary.setCertificatesEarned(0);
        summary.setCertificationName(null);
        summary.setCertificationProgress(0);
        return summary;
    }
}
