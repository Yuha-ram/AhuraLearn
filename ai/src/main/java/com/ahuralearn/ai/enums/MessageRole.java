package com.ahuralearn.ai.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Yorina
 * @since 2026-06-27
 */
@Getter
@AllArgsConstructor
public enum MessageRole implements BaseEnum {
    USER(1, "user"),
    ASSISTANT(2, "assistant"),
    SYSTEM(3, "system");

    private final Integer value;

    @EnumValue
    @JsonValue
    private final String desc;
}
