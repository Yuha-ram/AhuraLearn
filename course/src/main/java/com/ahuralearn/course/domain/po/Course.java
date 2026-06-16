package com.ahuralearn.course.domain.po;

import java.math.BigDecimal;

import com.ahuralearn.course.enums.CourseStatus;
import com.ahuralearn.course.enums.DifficultyLevel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Core course information table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course")
@Schema(description = "Course Entity")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Course title
     */
    private String name;

    /**
     * Short description or subtitle
     */
    private String subtitle;

    /**
     * Detailed course description
     */
    private String description;

    /**
     * Foreign key to category table
     */
    private Long categoryId;

    /**
     * Foreign key to instructor table
     */
    private Long instructorId;

    /**
     * Cloud URL for course cover image
     */
    private String coverUrl;

    /**
     * 0: Beginner, 1: Intermediate, 2: Advanced
     */
    private DifficultyLevel difficultyLevel;

    /**
     * Estimated hours to complete
     */
    private Integer hoursRequired;

    /**
     * Average course rating (e.g., 4.85)
     */
    private BigDecimal rating;

    /**
     * Total number of reviews
     */
    private Integer reviewCount;

    /**
     * Denormalized count of enrolled students
     */
    private Integer enrolledCount;

    /**
     * JSON array of learning outcomes
     */
    private String outcomes;

    /**
     * 0: DRAFT, 1: PUBLISHED, 2: UNPUBLISHED
     */
    private CourseStatus status;

    /**
     * Record creation time
     */
    private LocalDateTime createTime;

    /**
     * Record last update time
     */
    private LocalDateTime updateTime;


}
