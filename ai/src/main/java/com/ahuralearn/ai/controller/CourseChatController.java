package com.ahuralearn.ai.controller;

import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.ahuralearn.ai.service.ICourseChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/ai/course")
@RequiredArgsConstructor
@Tag(name = "AI Course Assistant")
public class CourseChatController {

    private final ICourseChatService courseChatService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Chat with AI")
    public SseEmitter chat(@RequestBody ChatRequestDTO request) {
        // 修改：Controller 仅负责创建 SSE 通道并转发请求。
        SseEmitter emitter = new SseEmitter(0L);
        courseChatService.chat(request, emitter);
        return emitter;
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get Session List")
    public List<ChatSessionVO> getSessionList() {
        return courseChatService.getSessionList();
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Get Messages of one session")
    public List<ChatMessageVO> getMessageHistory(@PathVariable Long sessionId) {
        return courseChatService.getMessageHistory(sessionId);
    }
}
