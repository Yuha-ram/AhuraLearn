package com.ahuralearn.ai.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用于区分前端历史消息的渲染类型。
 */
@Getter
@AllArgsConstructor
public enum ChatMessageType implements BaseEnum {
    TEXT(1, "text"),
    COURSE_CARD(2, "course_card");

    @EnumValue
    private final Integer value;
    private final String desc;
}
