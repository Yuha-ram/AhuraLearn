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
    DELETED(0, "deleted"),
    // 修改：pending 用于标记流式生成尚未完成的新会话。
    ACTIVE(1, "active"),
    PENDING(2, "pending"),
    // 修改：failed 用于保留首次生成失败且需要排障的会话。
    FAILED(3, "failed"),
    ;

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String desc;
}
