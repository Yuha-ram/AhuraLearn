package com.ahuralearn.learning.service.impl;

import com.ahuralearn.learning.domain.po.ExamPO;
import com.ahuralearn.learning.domain.po.ExamSubjectPO;
import com.ahuralearn.learning.domain.vo.MyExamVO;
import com.ahuralearn.learning.mapper.ExamMapper;
import com.ahuralearn.learning.mapper.ExamSubjectMapper;
import com.ahuralearn.learning.service.ExamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamMapper examMapper;
    private final ExamSubjectMapper examSubjectMapper;

    public ExamServiceImpl(ExamMapper examMapper,
                           ExamSubjectMapper examSubjectMapper) {
        this.examMapper = examMapper;
        this.examSubjectMapper = examSubjectMapper;
    }

    @Override
    public MyExamVO getMyExam(Long userId) {
        ExamPO bestExam = selectBestExam(userId);
        List<ExamSubjectPO> subjectPOList = selectSubjects(userId);
        List<ExamPO> recentExamPOList = selectRecentExams(userId);

        MyExamVO vo = new MyExamVO();

        if (bestExam != null) {
            MyExamVO.ResultVO resultVO = new MyExamVO.ResultVO();
            resultVO.setStatus(bestExam.getStatus().toUpperCase());
            resultVO.setTitle(bestExam.getCourseName());
            resultVO.setDescription(
                    "Congratulations! You've successfully demonstrated proficiency in this exam."
            );
            resultVO.setScore(bestExam.getScore());
            resultVO.setTotalScore(bestExam.getTotalScore());

            vo.setResult(resultVO);
        }

        vo.setSubjects(
                subjectPOList.stream().map(item -> {
                    MyExamVO.SubjectVO subjectVO = new MyExamVO.SubjectVO();
                    subjectVO.setId(item.getId());
                    subjectVO.setName(item.getSubjectName());
                    subjectVO.setScore(item.getScore());
                    return subjectVO;
                }).toList()
        );

        vo.setRecentExams(
                recentExamPOList.stream().map(item -> {
                    MyExamVO.RecentExamVO recentExamVO = new MyExamVO.RecentExamVO();
                    recentExamVO.setId(item.getId());
                    recentExamVO.setCourseName(item.getCourseName());
                    recentExamVO.setScore(item.getScore());
                    recentExamVO.setStatus(item.getStatus());
                    recentExamVO.setIcon(item.getIcon());
                    return recentExamVO;
                }).toList()
        );

        return vo;
    }

    private ExamPO selectBestExam(Long userId) {
        LambdaQueryWrapper<ExamPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPO::getUserId, userId)
                .orderByDesc(ExamPO::getScore)
                .last("limit 1");

        return examMapper.selectOne(wrapper);
    }

    private List<ExamSubjectPO> selectSubjects(Long userId) {
        LambdaQueryWrapper<ExamSubjectPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamSubjectPO::getUserId, userId)
                .orderByAsc(ExamSubjectPO::getId);

        return examSubjectMapper.selectList(wrapper);
    }

    private List<ExamPO> selectRecentExams(Long userId) {
        LambdaQueryWrapper<ExamPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamPO::getUserId, userId)
                .orderByDesc(ExamPO::getId);

        return examMapper.selectList(wrapper);
    }
}