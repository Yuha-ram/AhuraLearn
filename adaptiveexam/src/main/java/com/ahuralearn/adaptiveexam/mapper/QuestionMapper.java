package com.ahuralearn.adaptiveexam.mapper;

import com.ahuralearn.adaptiveexam.domain.po.QuestionBank;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<QuestionBank> {

    // =========================================================
    // 根据 moduleId (对应数据库 lesson_id 列) 查询题目列表
    //
    // 注意：SELECT * 返回的 lesson_id 列因驼峰映射会变成 lessonId，
    //       但 QuestionBank.moduleId 字段不被 Service 层使用，
    //       因此不影响业务逻辑。
    // =========================================================

    @Select("""
        SELECT *
        FROM question_bank
        WHERE lesson_id = #{moduleId}
    """)
    List<QuestionBank> selectQuestionsByModule(String moduleId);

    // =========================================================
    // 根据 moduleId (即 course_id) 查询课程名称
    // =========================================================
    @Select("""
        SELECT name
        FROM course
        WHERE id = #{moduleId}
    """)
    String getCourseNameById(String moduleId);
}