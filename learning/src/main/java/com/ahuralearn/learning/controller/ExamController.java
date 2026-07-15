package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.learning.domain.vo.MyExamVO;
import com.ahuralearn.learning.service.ExamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/api/v1/exam/my-exam")
    public Result<MyExamVO> getMyExam() {
        Long userId = UserContext.getUser();
        return Result.success(examService.getMyExam(userId));
    }
}
