package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.dto.GoalDTO;
import com.ahuralearn.learning.domain.po.WeeklyGoal;
import com.ahuralearn.learning.domain.vo.GoalVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LearningGoalMapper {
    List<GoalVO> selectUserGoals(@Param("userId") Long userId);

    GoalVO selectUserGoal(@Param("userId") Long userId, @Param("id") Long id);

    int insertGoal(WeeklyGoal goal);

    int updateGoal(@Param("userId") Long userId,
                   @Param("id") Long id,
                   @Param("dto") GoalDTO dto);

    int updateGoalCompletion(@Param("userId") Long userId,
                             @Param("id") Long id,
                             @Param("achieved") boolean achieved,
                             @Param("currentValue") Integer currentValue,
                             @Param("achievedDay") String achievedDay);

    int deleteGoal(@Param("userId") Long userId, @Param("id") Long id);
}
