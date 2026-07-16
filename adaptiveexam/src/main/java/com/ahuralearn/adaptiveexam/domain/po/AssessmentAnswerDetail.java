package com.ahuralearn.adaptiveexam.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assessment_answer_detail")
public class AssessmentAnswerDetail {

    // =========================================================
    // 答题详情主键
    // IdType.ASSIGN_ID = 雪花算法，以 String 存入 varchar(50)
    // =========================================================

    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    // =========================================================
    // 所属考试记录 ID
    //
    // assessment_record.id
    //        ↓
    // assessment_answer_detail.record_id
    // =========================================================

    private String recordId;


    // =========================================================
    // 题目 ID
    // =========================================================

    private String questionId;


    // =========================================================
    // 用户答案
    // =========================================================

    private String userAnswer;


    // =========================================================
    // 正确答案（数据库字段：correct_answer）
    // =========================================================

    private String correctAnswer;


    // =========================================================
    // 是否回答正确
    //
    // 加 @TableField("is_correct") 避免 MP 对 isXxx 字段的歧义
    // =========================================================

    @TableField("is_correct")
    private Boolean isCorrect;


    // =========================================================
    // 作答该题耗时（秒），数据库默认值 0
    // =========================================================

    private Integer timeSpent;


    // =========================================================
    // 时间字段
    //
    // 由 MybatisPlusMetaObjectHandler 自动填充
    // =========================================================

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}