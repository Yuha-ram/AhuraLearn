package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ExamReportVO {

    // 考试记录ID
    private String assessmentId;
    // Feedback score
    private Integer score;
    // 每道题结果，提交考试时返回（兼容旧接口）
    private List<TestResult> testResults;
//正确题数
    private Integer correctCount;
//总题数
    private Integer totalQuestions;
//正确率（百分比）
    private Double accuracyRate;
//总耗时（秒）
    private Integer timeTaken;
    //查看考试详情时返回
    private List<AssessmentDetailVO> details;

    @Data
    public static class TestResult {

        //题目ID
        private String id;
        //用户答案
        private String userAnswer;
        //正确答案
        private String correctAnswer;
        //是否正确
        private Boolean isCorrect;
        //本题耗时（秒）
        private Integer timeSpent;
    }
}