package com.ahuralearn.learning.domain.dto;

import lombok.Data;

@Data
public class LearningPlanQueryDTO {
    private Integer pageNum = 1;

    private Integer pageSize = 3;
}
