package com.ahuralearn.report.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class ReportVO {
    private ProficiencyVO proficiency;
    private List<Map<String, Object>> errors;
    private List<Map<String, Object>> knowledgeGap;
    private AiSuggestionVO aiSuggestion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProficiencyVO {
        private Integer score;
        private String level;
        private String description;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiSuggestionVO {
        private String title;
        private String keyword;
        private String topic;
        private String text;
        private String buttonText;
    }
}