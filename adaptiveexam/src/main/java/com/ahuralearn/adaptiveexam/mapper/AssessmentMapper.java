package com.ahuralearn.adaptiveexam.mapper;

import com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord;
import com.ahuralearn.adaptiveexam.domain.po.AssessmentAnswerDetail;
import com.ahuralearn.adaptiveexam.domain.vo.AssessmentDetailVO;
import com.ahuralearn.adaptiveexam.domain.vo.SkillMasteryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AssessmentMapper extends BaseMapper<AssessmentRecord> {


    // =========================================================
    // Dashboard
    //
    // 查询当前用户所有考试记录
    // 最新考试排在最前面
    // =========================================================

    @Select("""
        SELECT
            id,
            user_id AS userId,
            lesson_id AS lessonId,
            course_id AS courseId,
            module_id AS moduleId,
            score,
            accuracy,
            total_questions AS totalQuestions,
            correct_count AS correctCount,
            wrong_question_content AS wrongQuestionContent,
            time_taken AS timeTaken,
            create_time AS createTime,
            update_time AS updateTime
        FROM assessment_record
        WHERE user_id = #{userId}
        ORDER BY create_time DESC
        """)
    List<AssessmentRecord> selectRecordsByUser(Long userId);


    // =========================================================
    // Dashboard
    //
    // Skill Mastery 真实统计
    // =========================================================

    @Select("""
        SELECT
            COALESCE(q.topic, 'General') AS topic,

            COUNT(d.id) AS totalQuestions,

            SUM(
                CASE
                    WHEN d.is_correct = 1 THEN 1
                    ELSE 0
                END
            ) AS correctQuestions,

            ROUND(
                SUM(
                    CASE
                        WHEN d.is_correct = 1 THEN 1
                        ELSE 0
                    END
                ) * 100.0 / COUNT(d.id),
                2
            ) AS masteryRate

        FROM assessment_answer_detail d

        INNER JOIN assessment_record r
                ON d.record_id = r.id

        INNER JOIN question_bank q
                ON d.question_id = q.id

        WHERE r.user_id = #{userId}

        GROUP BY COALESCE(q.topic, 'General')

        ORDER BY masteryRate DESC
        """)
    List<SkillMasteryVO> selectSkillMasteryByUser(Long userId);


    // =========================================================
    // 根据考试 ID 查询考试记录
    // =========================================================

    @Select("""
        SELECT
            id,
            user_id AS userId,
            lesson_id AS lessonId,
            course_id AS courseId,
            module_id AS moduleId,
            score,
            accuracy,
            total_questions AS totalQuestions,
            correct_count AS correctCount,
            wrong_question_content AS wrongQuestionContent,
            time_taken AS timeTaken,
            create_time AS createTime,
            update_time AS updateTime
        FROM assessment_record
        WHERE id = #{recordId}
        """)
    AssessmentRecord selectRecordById(String recordId);


    // =========================================================
    // 根据考试 ID 查询原始答题详情
    // =========================================================

    @Select("""
        SELECT
            id,
            record_id AS recordId,
            question_id AS questionId,
            user_answer AS userAnswer,
            is_correct AS isCorrect,
            create_time AS createTime,
            update_time AS updateTime
        FROM assessment_answer_detail
        WHERE record_id = #{recordId}
        ORDER BY create_time ASC
        """)
    List<AssessmentAnswerDetail>
    selectDetailsByRecordId(String recordId);


    // =========================================================
    // Question Review
    //
    // 查询完整题目详情
    // =========================================================

    @Select("""
        SELECT
            d.question_id        AS questionId,
            q.question_text      AS question,
            q.options_json       AS optionsJson,
            q.type               AS type,
            q.difficulty         AS difficulty,
            q.topic              AS topic,
            d.user_answer        AS userAnswer,
            q.correct_answer     AS correctAnswer,
            d.is_correct         AS isCorrect

        FROM assessment_answer_detail d

        LEFT JOIN question_bank q
               ON d.question_id = q.id

        WHERE d.record_id = #{recordId}

        ORDER BY d.create_time ASC
        """)
    List<AssessmentDetailVO>
    selectAssessmentDetails(String recordId);


    // =========================================================
    // Skill Mastery 三维度指标
    // =========================================================

    /**
     * Accuracy 维度：用户所有考试的平均正确率
     * accuracy 字段存的是百分比数字（10~100）
     */
    @Select("""
        SELECT COALESCE(AVG(accuracy), 0.0)
        FROM assessment_record
        WHERE user_id = #{userId}
    """)
    Double selectAvgAccuracy(Long userId);


    /**
     * Speed 维度：平均每题耗时（秒）
     * time_taken = 本次考试总耗时（秒），total_questions = 题目数
     */
    @Select("""
        SELECT COALESCE(
            AVG(time_taken * 1.0 / NULLIF(total_questions, 0)),
            0.0
        )
        FROM assessment_record
        WHERE user_id = #{userId}
    """)
    Double selectAvgTimePerQuestion(Long userId);


    /**
     * Problem Solving 维度：difficulty >= 3 题目的整体正确率
     * 注意：需要 assessment_answer_detail.question_id JOIN question_bank.id
     *       若 question_id 不匹配，返回 0
     */
    @Select("""
        SELECT COALESCE(
            SUM(
                CASE
                    WHEN d.is_correct = 1
                     AND q.difficulty >= 3
                    THEN 1
                    ELSE 0
                END
            ) * 100.0
            / NULLIF(
                SUM(
                    CASE
                        WHEN q.difficulty >= 3
                        THEN 1
                        ELSE 0
                    END
                ),
                0
            ),
            0.0
        )
        FROM assessment_answer_detail d
        INNER JOIN assessment_record r
                ON d.record_id = r.id
        INNER JOIN question_bank q
                ON d.question_id = q.id
        WHERE r.user_id = #{userId}
    """)
    Double selectProblemSolvingRate(Long userId);
}