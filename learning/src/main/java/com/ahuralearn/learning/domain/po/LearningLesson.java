package com.ahuralearn.learning.domain.po;

import com.ahuralearn.learning.enums.LearningStatus;
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
 * User Course Progress Table
 * </p>
 *
 * @author Yorina
 * @since 2026-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("learning_lesson")
public class LearningLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary Key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Associated Course ID
     */
    private Long courseId;

    /**
     * Associated User ID
     */
    private Long userId;

    /**
     * Learning status: 0-Not Enrolled, 1-In Progress, 2-Completed
     */
    private LearningStatus status;

    /**
     * Number of completed sections
     */
    private Integer learnedSections;

    /**
     * ID of the section last learned
     */
    private Long latestSectionId;

    /**
     * Timestamp of the last learning activity
     */
    private LocalDateTime latestLearnTime;

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    /**
     * Update time
     */
    private LocalDateTime updateTime;


}
