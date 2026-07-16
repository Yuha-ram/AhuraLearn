package com.ahuralearn.adaptiveexam.service;

import com.ahuralearn.adaptiveexam.domain.vo.DashboardVO;

public interface IAssessmentDashboardService {

    /**
     * 获取Dashboard统计数据
     *
     * @param userId 当前用户ID
     * @return DashboardVO
     */
    DashboardVO getDashboard(Long userId);

}