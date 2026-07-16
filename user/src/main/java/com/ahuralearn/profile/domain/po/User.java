package com.ahuralearn.profile.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * User table
 * </p>
 * The "My Information" page lives on the shared {@code user} table (one table,
 * like the boss's repo) instead of a separate {@code user_profile} table. This
 * entity is the profile module's view of that table: it maps the identity columns
 * it displays ({@code username}, {@code email}, {@code avatar}, {@code bio}) plus
 * the extended page fields it owns (title, age … skills). Since the consolidated
 * table (2026-07-15) there is no separate display name — {@code username} doubles
 * as the name shown on the page — and {@code age}/{@code birthday} are typed
 * columns (TINYINT / DATE) instead of free text.
 * <p>
 * Auth-only columns ({@code password}, {@code role}, {@code status},
 * {@code last_login_time}) exist on the table but are <b>managed by the auth
 * module</b>, so they are intentionally not mapped here. Because updates go through
 * {@code updateById} (non-null fields only), saving the page never touches them.
 *
 * @author Dariush
 * @since 2026-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * primary key — the user's id (assigned by the auth module on registration)
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * login name — unique; also the display name shown on the profile
     */
    private String username;

    /**
     * email address — owned by the auth module
     */
    private String email;

    /**
     * professional title / role line, e.g. "Frontend & AI Learner"
     */
    private String title;

    /**
     * short about-me text
     */
    private String bio;

    /**
     * avatar image URL
     */
    private String avatar;

    /**
     * age shown on the page (TINYINT UNSIGNED in the table)
     */
    private Integer age;

    /**
     * gender label, e.g. "Female"
     */
    private String gender;

    /**
     * country or region, e.g. "United States"
     */
    private String region;

    /**
     * date of birth (DATE in the table; the page sends/receives ISO yyyy-MM-dd)
     */
    private LocalDate birthday;

    /**
     * highest education, e.g. "Bachelor of Science in Computer Science"
     */
    private String education;

    /**
     * current occupation, e.g. "Frontend Developer (Intern)"
     */
    private String occupation;

    /**
     * comma-separated skills
     */
    private String skills;

    /**
     * create time — filled by the DB default (CURRENT_TIMESTAMP)
     */
    private LocalDateTime createTime;

    /**
     * update time — refreshed by the DB on update (ON UPDATE CURRENT_TIMESTAMP)
     */
    private LocalDateTime updateTime;

}
