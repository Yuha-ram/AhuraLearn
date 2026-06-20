package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.dto.QuizQuestionDTO;
import com.ahuralearn.course.domain.po.PostClassQuizQuestion;
import com.ahuralearn.course.mapper.PostClassQuizQuestionMapper;
import com.ahuralearn.course.service.IPostClassQuizQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Post-class quiz questions table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
@Service
public class PostClassQuizQuestionServiceImpl extends ServiceImpl<PostClassQuizQuestionMapper, PostClassQuizQuestion> implements IPostClassQuizQuestionService {

    @Override
    public List<QuizQuestionDTO> getQuizQuestionsBySectionId(Long sectionId) {
        List<PostClassQuizQuestion> list = lambdaQuery()
                .eq(PostClassQuizQuestion::getSectionId, sectionId)
                .orderByAsc(PostClassQuizQuestion::getId)
                .list();
        if (CollUtils.isEmpty(list))
            return CollUtils.emptyList();

        return list.stream().map(po -> {
            QuizQuestionDTO dto = BeanUtils.copyBean(po, QuizQuestionDTO.class);
            dto.setQuestionId(po.getId());
            return dto;
        }).collect(Collectors.toList());
    }
}
