package com.ahuralearn.file.event;

/**
 * Published by the file module right after a file row is deleted, so other modules
 * can clean up whatever they keyed to that document (e.g. the assistant module drops
 * the stored tutor chat history) without the file module depending on them.
 *
 * @param fileId id of the deleted file
 * @param userId owner of the deleted file
 *
 * @author Dariush
 * @since 2026-07-03
 */
public record FileDeletedEvent(Long fileId, Long userId) {
}
