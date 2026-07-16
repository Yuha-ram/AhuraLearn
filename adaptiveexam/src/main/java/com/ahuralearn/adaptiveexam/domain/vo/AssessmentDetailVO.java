package com.ahuralearn.adaptiveexam.domain.vo;

import lombok.Data;

//考试详情用于 Assessment Result 页面以及 AI Feedback 页面
@Data
public class AssessmentDetailVO {

    //题目ID
    private String questionId;

    //题目内容
    private String question;

    /**
     * 题目选项(JSON)
     * 与 question_bank.options_json 对应
     */
    private String optionsJson;

    /**
     * 题目类型
     * multiple-choice
     * true-false
     * short-answer
     */
    private String type;

    //难度
    private Integer difficulty;


    //用户答案
    private String userAnswer;

    //正确答案
    private String correctAnswer;

    //是否答对
    private Boolean isCorrect;

    //所属知识点
    private String topic;
}