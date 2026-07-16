package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardVO {

    /* =========================
       Dashboard 总体统计
       ========================= */

    /* 总考试次数 */
    private Integer totalAttempts;

    /* 平均成绩 */
    private Double averageScore;

    /* 最高成绩 */
    private Integer highestScore;

    /* 最近一次成绩 */
    private Integer latestScore;

    /* 平均正确率 */
    private Double accuracyRate;

    /* 平均作答时间（秒） */
    private Double averageTime;


    /* =========================
       Skill Mastery
       ========================= */

    /* 按题目 topic 统计的真实技能掌握度 */
    private List<SkillMasteryVO> skills = new ArrayList<>();


    /* =========================
       Recent Assessments
       ========================= */

    /* 最近 5 次考试记录 */
    private List<RecentAssessmentVO> recentAssessments = new ArrayList<>();
}