package com.ahuralearn.ai.domain.po;

import com.ahuralearn.ai.enums.MessageRole;
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
 * Detail table storing individual messages within a session
 * </p>
 *
 * @author Yorina
 * @since 2026-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary Key: Unique identifier for each message
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Foreign Key: Links to chat_session.id
     */
    private Long sessionId;

    /**
     * Identity: user (student), assistant (AI model), or system (hidden prompts)
     */
    private MessageRole role;

    /**
     * Payload: The actual text content, markdown, or JSON response
     */
    private String content;

    /**
     * Ordering key: Determines the chronological order of chat bubbles
     */
    private LocalDateTime createTime;


}
