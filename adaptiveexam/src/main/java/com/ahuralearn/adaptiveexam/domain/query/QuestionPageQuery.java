package com.ahuralearn.adaptiveexam.domain.query;

import lombok.Data;

@Data
public class QuestionPageQuery {
    private Integer pageNo = 1;      // 当前页码 (默认第1页)
    private Integer pageSize = 10;   // 每页条数 (默认10条)

    // 以下是选填的搜索条件
    private String moduleId;         // 按课程筛选
    private String keyword;          // 按题干关键词模糊搜索
}