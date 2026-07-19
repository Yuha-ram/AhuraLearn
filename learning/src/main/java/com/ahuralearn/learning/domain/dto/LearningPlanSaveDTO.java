package com.ahuralearn.learning.domain.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Learning plan save DTO.
 *
 * @author GXC
 * @since 2026-06-28
 */
@Data
public class LearningPlanSaveDTO {
    private String title;

    private String studyTime;

    private String priority;

    private String dueText;

    private LocalDate dueDate;

    private String note;
}
