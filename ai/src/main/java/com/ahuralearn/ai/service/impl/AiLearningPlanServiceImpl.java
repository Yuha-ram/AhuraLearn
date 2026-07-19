package com.ahuralearn.ai.service.impl;

import com.ahuralearn.ai.domain.dto.AIChatRequestDTO;
import com.ahuralearn.ai.domain.dto.AiStudyPlanRequestDTO;
import com.ahuralearn.ai.domain.vo.AIChatResponseVO;
import com.ahuralearn.ai.domain.vo.AiStudyPlanVO;
import com.ahuralearn.ai.service.AiStudyPlanAgent;
import com.ahuralearn.ai.service.AiStudyPlanService;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.learning.domain.dto.LearningPlanSaveDTO;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import com.ahuralearn.learning.service.LearningPlanService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiLearningPlanServiceImpl implements AiStudyPlanService {

    private static final String FALLBACK_PLAN = "Week 1: Review core concepts and establish a daily study routine. "
            + "Week 2: Focus on weak areas with targeted lessons and exercises. "
            + "Week 3: Apply knowledge through practice projects and mock tests. "
            + "Week 4: Review mistakes, reinforce key topics, and complete a final assessment.";

    private final AiStudyPlanAgent aiStudyPlanAgent;
    private final LearningPlanService learningPlanService;
    private final ObjectMapper objectMapper;

    @Override
    public AiStudyPlanVO generatePlan(AiStudyPlanRequestDTO request) {
        try {
            String plan = aiStudyPlanAgent.generate(buildUserMessage(request));
            return new AiStudyPlanVO(StringUtils.hasText(plan) ? plan : FALLBACK_PLAN);
        } catch (RuntimeException exception) {
            log.warn("AI study plan generation failed; using fallback plan", exception);
            return new AiStudyPlanVO(FALLBACK_PLAN);
        }
    }

    @Override
    public LearningPlanVO generateAndSavePlan(AiStudyPlanRequestDTO request) {
        try {
            String rawJson = aiStudyPlanAgent.generatePlanForm(buildFormUserMessage(request));
            LearningPlanSaveDTO saveDTO = parsePlanForm(rawJson, request);
            return learningPlanService.createAiPlan(saveDTO);
        } catch (RuntimeException exception) {
            log.error("AI study plan form generation failed", exception);
            return learningPlanService.createAiPlan(buildFallbackPlanForm(request));
        }
    }

    @Override
    public AIChatResponseVO chat(AIChatRequestDTO request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getValue(), "Message cannot be empty");
        }

        try {
            String answer = aiStudyPlanAgent.chat(request.getMessage());
            return new AIChatResponseVO("assistant", answer);
        } catch (RuntimeException exception) {
            log.error("AI learning assistant request failed", exception);
            throw new BusinessException(ResultCode.SYSTEM_ERROR.getValue(),
                    "AI assistant is temporarily unavailable");
        }
    }

    @Override
    public SseEmitter generatePlanStream(AiStudyPlanRequestDTO request) {
        return startStream(aiStudyPlanAgent.generateStream(buildUserMessage(request)));
    }

    @Override
    public SseEmitter chatStream(AIChatRequestDTO request) {
        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getValue(), "Message cannot be empty");
        }
        return startStream(aiStudyPlanAgent.chatStream(request.getMessage()));
    }

    private String buildUserMessage(AiStudyPlanRequestDTO request) {
        return "Create a practical, structured study plan using the following learner profile.\n"
                + "Learning goal: " + request.getGoal() + "\n"
                + "Current level: " + request.getLevel() + "\n"
                + "Available study time: " + request.getAvailableTime() + "\n"
                + "Weak areas: " + request.getWeakness() + "\n"
                + "Return only the study plan.";
    }

    private String buildFormUserMessage(AiStudyPlanRequestDTO request) {
        return "Create one learning plan form record using the following learner profile.\n"
                + "Learning goal: " + request.getGoal() + "\n"
                + "Current level: " + request.getLevel() + "\n"
                + "Available study time: " + request.getAvailableTime() + "\n"
                + "Weak areas: " + request.getWeakness() + "\n"
                + "Today: " + LocalDate.now() + "\n";
    }

    private LearningPlanSaveDTO parsePlanForm(String rawJson, AiStudyPlanRequestDTO request) {
        try {
            JsonNode root = objectMapper.readTree(extractJson(rawJson));
            LearningPlanSaveDTO saveDTO = new LearningPlanSaveDTO();
            saveDTO.setTitle(defaultText(root, "title", buildFallbackTitle(request)));
            saveDTO.setStudyTime(defaultText(root, "studyTime", request.getAvailableTime()));
            saveDTO.setPriority(defaultText(root, "priority", "Medium"));
            saveDTO.setDueText(defaultText(root, "dueText", "This Week"));
            saveDTO.setNote(defaultText(root, "note", buildFallbackNote(request)));

            String dueDate = text(root, "dueDate");
            if (StringUtils.hasText(dueDate) && !"null".equalsIgnoreCase(dueDate.trim())) {
                saveDTO.setDueDate(LocalDate.parse(dueDate));
            }
            return saveDTO;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI study plan form JSON", e);
        }
    }

    private String extractJson(String rawText) {
        if (!StringUtils.hasText(rawText)) {
            throw new IllegalStateException("AI returned empty study plan form");
        }
        String text = rawText.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new IllegalStateException("AI response does not contain a JSON object");
        }
        return text.substring(start, end + 1);
    }

    private String defaultText(JsonNode root, String fieldName, String defaultValue) {
        String value = text(root, fieldName);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    private String text(JsonNode root, String fieldName) {
        JsonNode value = root.get(fieldName);
        return value == null || value.isNull() ? null : value.asText();
    }

    private LearningPlanSaveDTO buildFallbackPlanForm(AiStudyPlanRequestDTO request) {
        LearningPlanSaveDTO saveDTO = new LearningPlanSaveDTO();
        saveDTO.setTitle(buildFallbackTitle(request));
        saveDTO.setStudyTime(StringUtils.hasText(request.getAvailableTime()) ? request.getAvailableTime() : "2 hours per day");
        saveDTO.setPriority("Medium");
        saveDTO.setDueText("This Week");
        saveDTO.setNote(buildFallbackNote(request));
        return saveDTO;
    }

    private String buildFallbackTitle(AiStudyPlanRequestDTO request) {
        String goal = StringUtils.hasText(request.getGoal()) ? request.getGoal().trim() : "Study Plan";
        if (goal.length() > 80) {
            goal = goal.substring(0, 80).trim();
        }
        return goal;
    }

    private String buildFallbackNote(AiStudyPlanRequestDTO request) {
        return "Focus on " + request.getGoal()
                + ". Study at a " + request.getLevel()
                + " level for " + request.getAvailableTime()
                + ", with extra practice on " + request.getWeakness()
                + ". Review progress at the end of the week.";
    }

    private SseEmitter startStream(TokenStream tokenStream) {
        SseEmitter emitter = new SseEmitter(0L);
        tokenStream.onPartialResponse(token -> sendEvent(emitter, "text", token))
                .onCompleteResponse(response -> {
                    sendEvent(emitter, "done", "done");
                    emitter.complete();
                })
                .onError(error -> {
                    log.error("AI learning assistant stream failed", error);
                    try {
                        sendEvent(emitter, "error", "AI assistant is temporarily unavailable");
                        emitter.complete();
                    } catch (RuntimeException sendError) {
                        emitter.completeWithError(error);
                    }
                })
                .start();
        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data == null ? "" : data));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send AI study plan SSE event", e);
        }
    }
}
