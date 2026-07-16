package com.ahuralearn.adaptiveexam.controller;

import com.ahuralearn.adaptiveexam.domain.dto.SubmitExamDTO;
import com.ahuralearn.adaptiveexam.domain.vo.DashboardVO;
import com.ahuralearn.adaptiveexam.domain.vo.ExamReportVO;
import com.ahuralearn.adaptiveexam.service.IAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assessments")

@RequiredArgsConstructor
public class AssessmentController {

    private final IAssessmentService assessmentService;

    //学生交卷
    @PostMapping("/submit")
    public ExamReportVO submitExam(
            @Valid @RequestBody SubmitExamDTO dto) {
        return assessmentService.submitExam(dto);
    }

    //Dashboard 数据
    @GetMapping("/dashboard")
    public DashboardVO getDashboard() {
        return assessmentService.getDashboard();
    }


    //Feedback 页面
    @GetMapping("/report/{recordId}")
    public ExamReportVO getReport(
            @PathVariable String recordId) {
        return assessmentService.getReport(recordId);
    }

    //获取历史记录
    @GetMapping("/history")
    public java.util.List<com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord> getHistory() {
        Long currentUserId = 1L; // 模拟当前用户
        return assessmentService.getHistory(currentUserId);
    }
}