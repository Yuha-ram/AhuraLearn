package com.ahuralearn.profile.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * User table
 * </p>
 * The "My Information" page now lives on the shared {@code user} table (one table,
 * like the boss's repo) instead of a separate {@code user_profile} table. This
 * entity is the profile module's view of that table: it maps the identity columns
 * it displays ({@code username}, {@code email}, {@code avatar}, {@code bio}) plus
 * the extended page fields it owns (name, headline, age … skills).
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
     * login name — owned by the auth module, shown read-only on the page
     */
    private String username;

    /**
     * email address — owned by the auth module
     */
    private String email;

    /**
     * display name shown on the profile, e.g. "Alex Rivera"
     */
    private String name;

    /**
     * headline / role line, e.g. "Frontend & AI Learner"
     */
    private String headline;

    /**
     * short about-me text
     */
    private String bio;

    /**
     * avatar image URL
     */
    private String avatar;

    /**
     * age shown on the page, kept as entered (e.g. "24")
     */
    private String age;

    /**
     * gender label, e.g. "Female"
     */
    private String gender;

    /**
     * country or region, e.g. "United States"
     */
    private String region;

    /**
     * date of birth, shown as entered (e.g. "May 18, 2002")
     */
    private String birthday;

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
