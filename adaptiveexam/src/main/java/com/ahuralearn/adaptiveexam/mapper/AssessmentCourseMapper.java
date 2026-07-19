package com.ahuralearn.adaptiveexam.mapper;

import com.ahuralearn.adaptiveexam.domain.po.CourseModule;
import com.ahuralearn.adaptiveexam.domain.vo.CourseListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssessmentCourseMapper extends BaseMapper<CourseModule> {

    // =========================================================
    // 查询当前用户已选修（或已添加）且已发布的课程
    // =========================================================

    @Select("""
        SELECT DISTINCT
            c.id,
            c.name,
            CAST(c.id AS CHAR) AS value,
            c.name AS label
        FROM course c
        INNER JOIN learning_lesson ll ON c.id = ll.course_id
        WHERE ll.user_id = #{userId} AND c.status = 1
        ORDER BY c.name
    """)
    List<CourseListVO> getEnrolledCourses(Long userId);
}