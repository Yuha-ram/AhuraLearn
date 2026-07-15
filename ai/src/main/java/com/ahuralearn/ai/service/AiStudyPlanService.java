package com.ahuralearn.ai.service;

import com.ahuralearn.ai.domain.dto.AIChatRequestDTO;
import com.ahuralearn.ai.domain.dto.AiStudyPlanRequestDTO;
import com.ahuralearn.ai.domain.vo.AIChatResponseVO;
import com.ahuralearn.ai.domain.vo.AiStudyPlanVO;
import com.ahuralearn.learning.domain.vo.LearningPlanVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiStudyPlanService {

    AiStudyPlanVO generatePlan(AiStudyPlanRequestDTO request);

    LearningPlanVO generateAndSavePlan(AiStudyPlanRequestDTO request);

    AIChatResponseVO chat(AIChatRequestDTO request);

    SseEmitter generatePlanStream(AiStudyPlanRequestDTO request);

    SseEmitter chatStream(AIChatRequestDTO request);
}
