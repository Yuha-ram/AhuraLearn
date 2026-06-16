package com.ahuralearn.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * Course Section / Video Table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_section")
public class CourseSection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Section ID, Primary Key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Associated Chapter ID
     */
    private Long chapterId;

    /**
     * Course ID (denormalized for direct querying of all sections in a course)
     */
    private Long courseId;

    /**
     * Section title
     */
    private String title;

    /**
     * Brief summary / content description
     */
    private String description;

    /**
     * Cloud video URL or resource ID
     */
    private String videoUrl;

    /**
     * Video duration (in seconds, used for UI display like 12:30)
     */
    private Integer duration;

    /**
     * Local sort order within the chapter
     */
    private Integer sortOrder;

    /**
     * Creation timestamp
     */
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    private LocalDateTime updateTime;


}
