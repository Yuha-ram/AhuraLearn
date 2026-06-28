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
public enum SessionStatus implements BaseEnum {
    ACTIVE(1, "active"),
    DELETED(0, "deleted"),
    ;

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String desc;
}
