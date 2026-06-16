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
 * Course Chapter Table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_chapter")
public class CourseChapter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Chapter ID, Primary Key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Associated Course ID
     */
    private Long courseId;

    /**
     * Chapter title
     */
    private String title;

    /**
     * Chapter description
     */
    private String description;

    /**
     * Local sort order (determines the display sequence within the course)
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
