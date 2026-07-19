package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.vo.DashboardAchievementCountsVO;
import com.ahuralearn.learning.domain.vo.LearningCourseCardVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LearningDashboardMapper {
    Integer countCoursesByStatus(@Param("userId") Long userId, @Param("status") Integer status);

    DashboardAchievementCountsVO selectAchievementCounts(@Param("userId") Long userId);

    Integer selectOverallProgress(@Param("userId") Long userId);

    List<LearningCourseCardVO> selectLatestOngoingCourses(@Param("userId") Long userId);
}
