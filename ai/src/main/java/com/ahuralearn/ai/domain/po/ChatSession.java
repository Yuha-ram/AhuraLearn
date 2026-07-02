package com.ahuralearn.ai.domain.po;

import com.ahuralearn.ai.enums.SessionStatus;
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
 * Master table storing chat session metadata for UI sidebar
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_session")
public class ChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary Key: Unique identifier for each chat session
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Foreign Key: The ID of the student who owns this session
     */
    private Long userId;

    /**
     * Reserved field: For future AI module routing (e.g., course_recommender)
     */
    private String type;

    /**
     * UI Display: Automatically generated or extracted title for the sidebar
     */
    private String title;

    /**
     * Logical deletion flag: 1 for Active, 0 for Deleted
     */
    private SessionStatus status;

    /**
     * Audit: When the session was first created
     */
    private LocalDateTime createTime;

    /**
     * Ordering key: Automatically updates when a new message is added
     */
    private LocalDateTime updateTime;


}
