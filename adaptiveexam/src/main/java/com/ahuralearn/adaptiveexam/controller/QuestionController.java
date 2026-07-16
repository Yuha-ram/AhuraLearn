package com.ahuralearn.adaptiveexam.controller;

import com.ahuralearn.adaptiveexam.domain.dto.QuestionFormDTO;
import com.ahuralearn.adaptiveexam.domain.vo.ExamQuestionVO;
import com.ahuralearn.adaptiveexam.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // 🌟 导入校验注解
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private IQuestionService questionService;

    // 🌟 注意：这里用的是 PostMapping，并且参数加了 @RequestBody
    @PostMapping
    public String addQuestion(@Valid @RequestBody QuestionFormDTO dto) {
        String newQuestionId = questionService.addQuestion(dto);
        return "题目添加成功！新 ID: " + newQuestionId;
    }


// 🌟 新增：前端获取考卷接口
// 请求路径相当于：GET http://localhost:8080/api/questions/list?moduleId=c_001
@GetMapping("/list")
public List<ExamQuestionVO> getExamQuestions(@RequestParam String moduleId) {
    return questionService.getExamQuestions(moduleId);
  }
}
