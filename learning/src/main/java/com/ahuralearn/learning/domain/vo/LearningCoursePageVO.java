package com.ahuralearn.learning.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class LearningCoursePageVO {
    private Integer inProgressCourses;
    private List<LearningCourseCardVO> courses;
    private List<LearningCourseCategoryVO> categories;
}
