package com.ahuralearn.report.service;

import com.ahuralearn.report.domain.vo.ReportCourseVO;
import com.ahuralearn.report.domain.vo.ReportVO;

import java.util.List;

public interface ReportService {
    ReportVO getReport(Long courseId);
    List<ReportCourseVO> getReportCourses();
}