package com.ahuralearn.course.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.ahuralearn.common.utils.StringUtils;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DifficultyLevel implements BaseEnum {
    BEGINNER(0, "beginner"),
    INTERMEDIATE(1, "intermediate"),
    ADVANCED(2, "advanced"),
    ;

    @EnumValue
    Integer value;
    @JsonValue
    String desc;

    public static Integer getSafeValue(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        try {
            return DifficultyLevel.valueOf(name.toUpperCase()).getValue();
        } catch (IllegalArgumentException e) {
            // for save, e.g., 'HARD' invalid params
            return null;
        }
    }
}
