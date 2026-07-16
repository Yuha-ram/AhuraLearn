package com.ahuralearn.adaptiveexam.ai.service;

import com.ahuralearn.adaptiveexam.ai.assistant.AdaptiveAssessmentAssistant;
import com.ahuralearn.adaptiveexam.domain.po.QuestionBank;
import com.ahuralearn.adaptiveexam.mapper.QuestionMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AIQuestionGenerationService {

    @Autowired
    private AdaptiveAssessmentAssistant assistant;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 调用 AI 生成题目，解析 JSON，并存入 question_bank
     */
    public void generateAndSaveQuestions(String moduleId, String topic, int count) {
        // 1. 调用 AI 获取 JSON 字符串
        String jsonResult = assistant.generateQuestions("请开始出题", topic, count);

        try {
            // 提取 JSON 数组部分，防止大模型带有前言或后语
            if (jsonResult != null) {
                int startIndex = jsonResult.indexOf("[");
                int endIndex = jsonResult.lastIndexOf("]");
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    jsonResult = jsonResult.substring(startIndex, endIndex + 1);
                } else {
                    throw new IllegalStateException("未能从大模型返回结果中提取到有效的 JSON 数组: " + jsonResult);
                }
            }

            // 2. 解析 JSON
            List<Map<String, Object>> questions = objectMapper.readValue(jsonResult, new TypeReference<>() {});

            // 3. 转换为 QuestionBank POJO 并保存
            for (Map<String, Object> q : questions) {
                QuestionBank po = new QuestionBank();
                po.setModuleId(moduleId); // 所属模块
                po.setQuestionText((String) q.get("question_text"));
                
                // 将 options 列表重新转为 JSON 字符串存入 options_json
                List<String> options = (List<String>) q.get("options_json");
                po.setOptionsJson(objectMapper.writeValueAsString(options));
                
                po.setCorrectAnswer((String) q.get("correct_answer"));
                po.setDifficulty((Integer) q.get("difficulty"));
                po.setTopic((String) q.get("topic"));
                po.setType((String) q.get("type"));

                questionMapper.insert(po);
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 出题解析并保存失败", e);
        }
    }
}
