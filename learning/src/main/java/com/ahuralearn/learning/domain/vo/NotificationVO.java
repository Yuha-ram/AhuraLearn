package com.ahuralearn.learning.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotificationVO {
    private Long id;

    private String title;

    private String content;

    private String details;

    private String type;

    private Boolean acknowledged;

    private Long relatedId;

    private LocalDate dueDate;

    private String dueDay;

    private Long daysLeft;

    private String priority;

    private String status;

    private String description;

    private Integer progress;

    private Integer estimatedMinutes;

    private String courseName;

    private List<String> nextSteps;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @JsonProperty("isAcknowledged")
    public Boolean getIsAcknowledged() {
        return acknowledged;
    }
}
