package com.ahuralearn.ai.controller;

import com.ahuralearn.ai.domain.dto.ChatRequestDTO;
import com.ahuralearn.ai.domain.vo.ChatResponseVO;
import com.ahuralearn.ai.domain.vo.ChatMessageVO;
import com.ahuralearn.ai.domain.vo.ChatSessionVO;
import com.ahuralearn.ai.service.ICourseChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai/course")
@RequiredArgsConstructor
@Tag(name = "AI Course Assistant")
public class CourseChatController {

    private final ICourseChatService courseChatService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI")
    public ChatResponseVO chat(@RequestBody ChatRequestDTO request) {
        return courseChatService.chat(request);
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



