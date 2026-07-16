package com.ahuralearn.adaptiveexam.service.impl;

import com.ahuralearn.adaptiveexam.domain.po.QuestionBank;
import com.ahuralearn.adaptiveexam.domain.vo.ExamQuestionVO;
import com.ahuralearn.adaptiveexam.mapper.QuestionMapper;
import com.ahuralearn.adaptiveexam.service.IQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ahuralearn.adaptiveexam.ai.service.AIQuestionGenerationService;
import com.ahuralearn.adaptiveexam.domain.dto.QuestionFormDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionServiceImpl
        implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AIQuestionGenerationService aiQuestionService;

    @Override
    public List<ExamQuestionVO> getExamQuestions(String moduleId) {
        // 兼容整合版前端
        if ("latest".equals(moduleId)) {
            moduleId = "c_001";
        }

        List<QuestionBank> poList =
                questionMapper.selectQuestionsByModule(moduleId);

        // 如果题库数量少于 40 题，调用 AI 自动补充
        if (poList.size() < 40) {
            // 根据 moduleId 从 course 表查询真实的课程名称
            String topic = questionMapper.getCourseNameById(moduleId);
            if (topic == null || topic.isBlank()) {
                topic = "General Knowledge";
            }
            
            int needed = 40 - poList.size();
            // 防止一次生成太多导致 AI 超时，最多一次请求 10 题
            if (needed > 10) needed = 10;
            
            aiQuestionService.generateAndSaveQuestions(moduleId, topic, needed);
            // 重新拉取刚刚保存的题目
            poList = questionMapper.selectQuestionsByModule(moduleId);
        }

        // 随机打乱并只取 5 题返回给前端
        Collections.shuffle(poList);
        if (poList.size() > 5) {
            poList = poList.subList(0, 5);
        }

        List<ExamQuestionVO> voList =
                new ArrayList<>();

        ObjectMapper mapper =
                new ObjectMapper();

        for (QuestionBank po : poList) {

            ExamQuestionVO vo =
                    new ExamQuestionVO();

            vo.setId(po.getId());
            vo.setType(po.getType() != null ? po.getType() : "multiple-choice");
            vo.setDifficulty(po.getDifficulty() != null ? po.getDifficulty() : 3);
            vo.setTopic(po.getTopic() != null ? po.getTopic() : "General");
            
            vo.setQuestion(po.getQuestionText());
            vo.setCorrectAnswer(po.getCorrectAnswer());

            try {
                List<ExamQuestionVO.OptionVO> optionVOList = new ArrayList<>();
                
                if (po.getOptionsJson() != null && !po.getOptionsJson().isBlank() && !"null".equals(po.getOptionsJson())) {
                    List<String> rawOptions = mapper.readValue(po.getOptionsJson(), List.class);
                    
                    String[] ids = {
                            "first",
                            "second",
                            "third",
                            "fourth"
                    };

                    for (int i = 0; i < rawOptions.size() && i < ids.length; i++) {
                        ExamQuestionVO.OptionVO optionVO = new ExamQuestionVO.OptionVO();
                        optionVO.setId(ids[i]);
                        optionVO.setText(rawOptions.get(i));
                        optionVOList.add(optionVO);
                    }
                }
                
                vo.setOptions(optionVOList);

            } catch (Exception e) {
                System.err.println("解析题目选项失败: " + po.getId());
            }

            voList.add(vo);
        }

        return voList;
    }
    @Override
    public String addQuestion(QuestionFormDTO dto) {
        // 暂时不实现题库新增
        return "success";
    }
}