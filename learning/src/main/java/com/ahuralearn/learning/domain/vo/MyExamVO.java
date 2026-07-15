package com.ahuralearn.learning.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class MyExamVO {

    private ResultVO result;

    private List<SubjectVO> subjects;

    private List<RecentExamVO> recentExams;

    @Data
    public static class ResultVO {

        private String status;

        private String title;

        private String description;

        private Integer score;

        private Integer totalScore;
    }

    @Data
    public static class SubjectVO {

        private Long id;

        private String name;

        private Integer score;
    }

    @Data
    public static class RecentExamVO {

        private Long id;

        private String courseName;

        private Integer score;

        private String status;

        private String icon;
    }
}