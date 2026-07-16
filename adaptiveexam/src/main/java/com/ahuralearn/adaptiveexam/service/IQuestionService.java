package com.ahuralearn.adaptiveexam.service;
import com.ahuralearn.adaptiveexam.domain.dto.QuestionFormDTO;
import com.ahuralearn.adaptiveexam.domain.vo.ExamQuestionVO; // 🌟 新增导包
import java.util.List; // 🌟 新增导包

public interface IQuestionService {
    String addQuestion(QuestionFormDTO dto);
    List<ExamQuestionVO> getExamQuestions(String moduleId);
}