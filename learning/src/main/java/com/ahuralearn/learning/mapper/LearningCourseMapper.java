package com.ahuralearn.learning.mapper;

import com.ahuralearn.learning.domain.dto.LearningCourseQueryDTO;
import com.ahuralearn.learning.domain.vo.LearningCourseCardVO;
import com.ahuralearn.learning.domain.vo.LearningCourseCategoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LearningCourseMapper {
    List<LearningCourseCardVO> selectUserCourses(@Param("userId") Long userId,
                                                  @Param("query") LearningCourseQueryDTO query);

    Integer countInProgressCourses(@Param("userId") Long userId);

    List<LearningCourseCategoryVO> selectUserCourseCategories(@Param("userId") Long userId);
}
