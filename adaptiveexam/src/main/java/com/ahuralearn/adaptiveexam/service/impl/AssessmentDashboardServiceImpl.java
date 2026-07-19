package com.ahuralearn.adaptiveexam.service.impl;

import com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord;
import com.ahuralearn.adaptiveexam.domain.vo.DashboardVO;
import com.ahuralearn.adaptiveexam.mapper.AssessmentMapper;
import com.ahuralearn.adaptiveexam.service.IAssessmentDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssessmentDashboardServiceImpl implements IAssessmentDashboardService {

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Override
    public DashboardVO getDashboard(Long userId) {

        List<AssessmentRecord> records =
                assessmentMapper.selectRecordsByUser(userId);

        DashboardVO dashboard = new DashboardVO();

        if (records == null || records.isEmpty()) {

            dashboard.setTotalAttempts(0);
            dashboard.setLatestScore(0);
            dashboard.setHighestScore(0);
            dashboard.setAverageScore((double) 0);
            dashboard.setAverageTime((double) 0);
            dashboard.setAccuracyRate((double) 0);

            return dashboard;
        }

        int totalAttempts = records.size();

        int latestScore = records.get(0).getScore();

        int highestScore = 0;

        int scoreSum = 0;

        int timeSum = 0;

        for (AssessmentRecord record : records) {

            scoreSum += record.getScore();

            timeSum += record.getTimeTaken();

            if (record.getScore() > highestScore) {
                highestScore = record.getScore();
            }

        }

        double averageScore = (double) scoreSum / totalAttempts;

        int averageTime = timeSum / totalAttempts;

        dashboard.setTotalAttempts(totalAttempts);

        dashboard.setLatestScore(latestScore);

        dashboard.setHighestScore(highestScore);

        dashboard.setAverageScore(averageScore);

        dashboard.setAverageTime((double) averageTime);

        // 第一版暂时使用平均分作为Accuracy
        dashboard.setAccuracyRate(averageScore);

        return dashboard;

    }

}