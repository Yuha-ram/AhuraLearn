package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

@Data
public class SkillMasteryVO {

    /* 技能维度名称，例如 Accuracy / Speed / Problem Solving */
    private String topic;

    /* 该维度掌握度百分比：0 - 100 */
    private Double masteryRate;

    /* 进度条下方说明文字（不同维度有不同含义） */
    private String subtitle;

    /* -------------------------------------------------------
     * 以下两个字段保留兼容性，可选填
     * ------------------------------------------------------- */

    /* 该主题累计作答题数（topic 分组模式下使用） */
    private Integer totalQuestions;

    /* 该主题累计答对题数（topic 分组模式下使用） */
    private Integer correctQuestions;
}