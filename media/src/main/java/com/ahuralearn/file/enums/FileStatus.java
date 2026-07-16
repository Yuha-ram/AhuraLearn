package com.ahuralearn.file.enums;

import com.ahuralearn.common.enums.BaseEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lifecycle status of an uploaded file.
 * <p>
 * Each constant pairs an int code with a human-readable label. The two
 * annotations decide how the enum crosses the DB and API boundaries:
 * <ul>
 *   <li>{@code @EnumValue} — MyBatis-Plus persists the int {@code value} in the DB column.</li>
 *   <li>{@code @JsonValue} — Jackson serializes the enum as the {@code desc} string in responses.</li>
 * </ul>
 * So the database stores e.g. {@code 1}, while the frontend receives {@code "Uploaded"}.
 *
 * @author Dariush
 * @since 2026-06-18
 */
@AllArgsConstructor
@Getter
public enum FileStatus implements BaseEnum {
    UPLOADED(1, "Uploaded"),       // received & processed successfully, usable
    FAILED(4, "Failed"),           // extraction/summary failed (file still stored)
    ;

    /** int code persisted to the DB */
    @EnumValue
    Integer value;

    /** label sent to the frontend in JSON */
    @JsonValue
    String desc;
}
