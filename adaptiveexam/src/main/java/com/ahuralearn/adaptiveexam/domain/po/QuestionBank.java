package com.ahuralearn.adaptiveexam.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("question_bank")
public class QuestionBank {

    // =========================================================
    // 主键
    // 题库 ID 由 MP 雪花算法自动生成
    // =========================================================

    @TableId(type = IdType.ASSIGN_ID)
    private String id;


    // =========================================================
    // 归属课程/章节 ID
    //
    // 数据库列名：lesson_id
    // Java 字段名：moduleId（与 Service 层参数名保持一致）
    // @TableField 告诉 MP 映射到 lesson_id 列
    // =========================================================

    @TableField("lesson_id")
    private String moduleId;


    // =========================================================
    // 题目内容字段（列名与驼峰一致，无需额外注解）
    // =========================================================

    private String questionText;

    // 数据库里存的是 JSON 字符串
    private String optionsJson;

    private String correctAnswer;

    private String type;

    private Integer difficulty;

    private String topic;
}