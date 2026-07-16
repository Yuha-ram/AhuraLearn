package com.ahuralearn.report.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.report.domain.vo.ReportCourseVO;
import com.ahuralearn.report.domain.vo.ReportVO;
import com.ahuralearn.report.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/v1/report")
    public Result<ReportVO> getReport(@RequestParam Long courseId) {
        return Result.success(reportService.getReport(courseId));
    }

    @GetMapping("/api/v1/report/courses")
    public Result<List<ReportCourseVO>> getReportCourses() {
        return Result.success(reportService.getReportCourses());
    }
}