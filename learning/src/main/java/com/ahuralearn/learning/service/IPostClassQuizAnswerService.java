package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.QuizAnswerDTO;
import com.ahuralearn.learning.domain.po.PostClassQuizAnswer;
import com.ahuralearn.learning.domain.vo.QuizDisplayVO;
import com.ahuralearn.learning.domain.vo.QuizOverviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * User quiz answer records table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
public interface IPostClassQuizAnswerService extends IService<PostClassQuizAnswer> {

    List<QuizDisplayVO> getQuizDetails(Long sectionId);

    void submitQuiz(Long sectionId, List<QuizAnswerDTO> answers);

    QuizOverviewVO getQuizOverview(Long sectionId);
}
