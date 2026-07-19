package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.vo.MyExamVO;

public interface ExamService {

    MyExamVO getMyExam(Long userId);
}