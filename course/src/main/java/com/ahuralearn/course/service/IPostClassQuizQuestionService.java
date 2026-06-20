package com.ahuralearn.course.service;

import com.ahuralearn.course.domain.dto.QuizQuestionDTO;
import com.ahuralearn.course.domain.po.PostClassQuizQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Post-class quiz questions table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
public interface IPostClassQuizQuestionService extends IService<PostClassQuizQuestion> {
    List<QuizQuestionDTO> getQuizQuestionsBySectionId(Long sectionId);
}
