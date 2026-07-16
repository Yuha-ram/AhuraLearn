package com.ahuralearn.adaptiveexam.ai.service;

import com.ahuralearn.adaptiveexam.ai.assistant.AdaptiveAssessmentAssistant;
import com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord;
import com.ahuralearn.adaptiveexam.mapper.AssessmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIChatService {

    @Autowired
    private AdaptiveAssessmentAssistant assistant;

    @Autowired
    private AssessmentMapper assessmentMapper;

    /**
     * 发起 AI 聊天，自动携带上次考试结果作为上下文
     */
    public dev.langchain4j.service.TokenStream chat(Long userId, String message, String recordId) {
        String context = "暂无近期考试数据。";
        
        if (recordId != null && !recordId.isEmpty()) {
            AssessmentRecord record = assessmentMapper.selectById(recordId);
            if (record != null) {
                context = String.format("学生最近一次考试得分: %d, 正确率: %.2f%%, 错题知识点: %s",
                        record.getScore(),
                        record.getAccuracy() != null ? record.getAccuracy() * 100 : 0.0,
                        record.getWrongQuestionContent());
            }
        }

        // 调用大模型
        return assistant.chatWithStudent(message, context);
    }
}
