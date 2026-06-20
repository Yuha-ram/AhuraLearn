package com.ahuralearn.learning.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.course.domain.dto.QuizQuestionDTO;
import com.ahuralearn.course.service.IPostClassQuizQuestionService;
import com.ahuralearn.learning.domain.dto.QuizAnswerDTO;
import com.ahuralearn.learning.domain.po.PostClassQuizAnswer;
import com.ahuralearn.learning.domain.vo.QuizDisplayVO;
import com.ahuralearn.learning.domain.vo.QuizOverviewVO;
import com.ahuralearn.learning.mapper.PostClassQuizAnswerMapper;
import com.ahuralearn.learning.service.IPostClassQuizAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * User quiz answer records table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-20
 */
@Service
@RequiredArgsConstructor
public class PostClassQuizAnswerServiceImpl extends ServiceImpl<PostClassQuizAnswerMapper, PostClassQuizAnswer> implements IPostClassQuizAnswerService {

    private final IPostClassQuizQuestionService questionService;

    @Override
    public List<QuizDisplayVO> getQuizDetails(Long sectionId) {
        if (sectionId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        List<QuizDisplayVO> voList = new ArrayList<>();
        List<QuizQuestionDTO> questions = questionService.getQuizQuestionsBySectionId(sectionId);
        if (CollUtils.isEmpty(questions))
            return voList;

        Long userId = UserContext.getUser();
        List<PostClassQuizAnswer> answers = lambdaQuery()
                .eq(PostClassQuizAnswer::getUserId, userId)
                .eq(PostClassQuizAnswer::getSectionId, sectionId)
                .orderByAsc(PostClassQuizAnswer::getId)
                .list();
        if (CollUtils.isEmpty(answers)) { // user hasn't taken quiz yet
            questions.forEach(q -> q.setCorrectAnswer(null)); // avoid answer leaked
            return BeanUtils.copyList(questions, QuizDisplayVO.class);
        }
        // organize the mapping of questions and user's answers
        Map<Long, PostClassQuizAnswer> map = answers.stream().collect(Collectors.toMap(PostClassQuizAnswer::getQuestionId, a -> a));
        for (QuizQuestionDTO question : questions) {
            Long questionId = question.getQuestionId();
            PostClassQuizAnswer answer = map.get(questionId);
            QuizDisplayVO vo = BeanUtils.copyBean(question, QuizDisplayVO.class);
            if (answer != null) {
                vo.setUserAnswer(answer.getUserAnswer())
                        .setIsCorrect(answer.getIsCorrect())
                        .setEarnedScore(answer.getEarnedScore());
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional
    public void submitQuiz(Long sectionId, List<QuizAnswerDTO> answers) {
        if (sectionId == null || CollUtils.isEmpty(answers))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        Long userId = UserContext.getUser();
        Long attemptCount = lambdaQuery()
                .eq(PostClassQuizAnswer::getUserId, userId)
                .eq(PostClassQuizAnswer::getSectionId, sectionId)
                .count();
        if (attemptCount > 0) // already taken the quiz before
            throw new BusinessException("Quiz already submitted for this section");

        List<QuizQuestionDTO> questions = questionService.getQuizQuestionsBySectionId(sectionId);
        if (CollUtils.isEmpty(questions))
            throw new BusinessException("Invalid section or no questions found");

        // key is questionId, val is question itself
        Map<Long, QuizQuestionDTO> map = questions.stream().collect(Collectors.toMap(QuizQuestionDTO::getQuestionId, q -> q));

        ArrayList<PostClassQuizAnswer> poList = new ArrayList<>(answers.size());
        for (QuizAnswerDTO answer : answers) {
            PostClassQuizAnswer po = BeanUtils.copyBean(answer, PostClassQuizAnswer.class);
            po.setUserId(userId).setSectionId(sectionId);

            QuizQuestionDTO questionDTO = map.get(answer.getQuestionId());
            // Mark as correct ONLY if question exists and answers match exactly
            if (questionDTO != null &&
                    StringUtils.isNotBlank(questionDTO.getCorrectAnswer()) &&
                    questionDTO.getCorrectAnswer().equals(answer.getUserAnswer())) {
                po.setIsCorrect(true).setEarnedScore(questionDTO.getScore());
            } else { // Mismatch or invalid questionId
                po.setIsCorrect(false).setEarnedScore(0);
            }
            poList.add(po);
        }
        saveBatch(poList);
    }

    @Override
    public QuizOverviewVO getQuizOverview(Long sectionId) {
        if (sectionId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        Long userId = UserContext.getUser();
        List<PostClassQuizAnswer> answers = lambdaQuery()
                .eq(PostClassQuizAnswer::getUserId, userId)
                .eq(PostClassQuizAnswer::getSectionId, sectionId)
                .list();

        List<QuizQuestionDTO> questions = questionService.getQuizQuestionsBySectionId(sectionId);
        int totalScore = 0;
        if (CollUtils.isNotEmpty(questions)) {
            totalScore = questions.stream().mapToInt(QuizQuestionDTO::getScore).sum();
        }
        if (CollUtils.isEmpty(answers)) { //hasn't taken the quiz yet
            return new QuizOverviewVO(false, null, totalScore, null);
        }

        int earnedScore = answers.stream().mapToInt(PostClassQuizAnswer::getEarnedScore).sum();
        return new QuizOverviewVO(true, earnedScore, totalScore, answers.getFirst().getCreateTime());
    }
}
