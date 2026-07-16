package com.ahuralearn.adaptiveexam.service;

import com.ahuralearn.adaptiveexam.domain.dto.SubmitExamDTO;
import com.ahuralearn.adaptiveexam.domain.vo.DashboardVO;
import com.ahuralearn.adaptiveexam.domain.vo.ExamReportVO;

public interface IAssessmentService {

    /**
     * 提交考试
     */
    ExamReportVO submitExam(SubmitExamDTO dto);

    /**
     * Dashboard 数据面板
     */
    DashboardVO getDashboard();

    /**
     * 获取历史记录
     */
    java.util.List<com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord> getHistory(Long userId);

    /**
     * 查看考试报告
     */
    ExamReportVO getReport(String recordId);

}