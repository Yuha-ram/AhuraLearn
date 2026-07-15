package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.vo.AchievementSummaryVO;
import org.apache.ibatis.annotations.Param;

public interface LearningAchievementMapper {
    AchievementSummaryVO selectSummaryByUserId(@Param("userId") Long userId);
}
