package com.ahuralearn.adaptiveexam.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("assessment_record")
public class AssessmentRecord {

    // =========================================================
    // 主键
    // IdType.ASSIGN_ID = 雪花算法，生成 Long 数字后以 String 存入 varchar(50)
    // =========================================================

    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    // =========================================================
    // 用户与课程信息
    // =========================================================

    // user 表主键类型为 bigint，与 DB 保持一致
    private Long userId;

    private Long lessonId;

    private Long courseId;

    private String moduleId;

    private Integer status; // 1: in progress, 2: completed


    // =========================================================
    // 考试结果
    // =========================================================

    private Integer score;

    private Double accuracy;

    private Integer totalQuestions;

    private Integer correctCount;


    // =========================================================
    // 错题内容
    //
    // 数据库类型：JSON
    //
    // Java 当前使用 String 保存 JSON：
    //
    // [
    //   "Question A",
    //   "Question B"
    // ]
    // =========================================================

    private String wrongQuestionContent;


    // =========================================================
    // 考试总耗时（秒）
    // =========================================================

    private Integer timeTaken;


    // =========================================================
    // 时间字段
    //
    // 对应数据库：
    // create_time
    // update_time
    //
    // 由 MybatisPlusMetaObjectHandler 自动填充
    // =========================================================

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}