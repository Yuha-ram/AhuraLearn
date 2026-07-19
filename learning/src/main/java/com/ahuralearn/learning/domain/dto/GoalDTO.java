package com.ahuralearn.learning.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GoalDTO {
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 50)
    private String type;

    @NotNull
    @Min(0)
    private Integer currentValue;

    @NotNull
    @Min(1)
    private Integer totalValue;

    @NotBlank
    @Size(max = 20)
    private String dueDay;
}
