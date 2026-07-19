package com.ahuralearn.ai.controller;

import com.ahuralearn.ai.domain.dto.AIChatRequestDTO;
import com.ahuralearn.ai.domain.dto.AiStudyPlanRequestDTO;
import com.ahuralearn.ai.domain.vo.AIChatResponseVO;
import com.ahuralearn.ai.domain.vo.AiStudyPlanVO;
import com.ahuralearn.ai.service.AiStudyPlanService;
import com.ahuralearn.common.domain.Result;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/ai/study-plan")
@Tag(name = "AI Study Plan")
@RequiredArgsConstructor
public class AiLearningPlanController {

    private final AiStudyPlanService aiStudyPlanService;

    @Operation(summary = "Generate an AI study plan")
    @PostMapping("/generate")
    public Result<AiStudyPlanVO> generatePlan(@Valid @RequestBody AiStudyPlanRequestDTO request) {
        return Result.success(aiStudyPlanService.generatePlan(request));
    }

    @Operation(summary = "Generate and save an AI study plan")
    @PostMapping("/generate/save")
    public Result<LearningPlanVO> generateAndSavePlan(@Valid @RequestBody AiStudyPlanRequestDTO request) {
        return Result.success(aiStudyPlanService.generateAndSavePlan(request));
    }

    @Operation(summary = "Generate an AI study plan as stream")
    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generatePlanStream(@Valid @RequestBody AiStudyPlanRequestDTO request) {
        return aiStudyPlanService.generatePlanStream(request);
    }

    @Operation(summary = "Chat with the AI learning assistant")
    @PostMapping("/chat")
    public Result<AIChatResponseVO> chat(@Valid @RequestBody AIChatRequestDTO request) {
        return Result.success(aiStudyPlanService.chat(request));
    }

    @Operation(summary = "Chat with the AI learning assistant as stream")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@Valid @RequestBody AIChatRequestDTO request) {
        return aiStudyPlanService.chatStream(request);
    }
}
