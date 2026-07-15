package com.ahuralearn.assistant.listener;

import com.ahuralearn.assistant.domain.po.ChatMessage;
import com.ahuralearn.assistant.service.IChatMessageService;
import com.ahuralearn.file.event.FileDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * File deletion listener
 * </p>
 * When the file module deletes a document (it publishes {@link FileDeletedEvent}),
 * drop that document's stored tutor conversation so no orphaned {@code assistant_chat_message}
 * rows accumulate. Scoped to the owner as defence-in-depth.
 *
 * @author Dariush
 * @since 2026-07-03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileDeletedListener {

    private final IChatMessageService chatMessageService;

    @EventListener
    public void onFileDeleted(FileDeletedEvent event) {
        boolean removed = chatMessageService.lambdaUpdate()
                .eq(ChatMessage::getUserId, event.userId())
                .eq(ChatMessage::getDocumentId, event.fileId())
                .remove();
        if (removed)
            log.info("Deleted tutor chat history for removed document {}", event.fileId());
    }
}
