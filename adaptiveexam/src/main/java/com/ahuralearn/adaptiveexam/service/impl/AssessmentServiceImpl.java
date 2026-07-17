package com.ahuralearn.adaptiveexam.service.impl;

import com.ahuralearn.adaptiveexam.domain.dto.SubmitExamDTO;
import com.ahuralearn.adaptiveexam.domain.po.AssessmentAnswerDetail;
import com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord;
import com.ahuralearn.adaptiveexam.domain.po.QuestionBank;
import com.ahuralearn.adaptiveexam.domain.vo.AssessmentDetailVO;
import com.ahuralearn.adaptiveexam.domain.vo.DashboardVO;
import com.ahuralearn.adaptiveexam.domain.vo.ExamReportVO;
import com.ahuralearn.adaptiveexam.domain.vo.RecentAssessmentVO;
import com.ahuralearn.adaptiveexam.domain.vo.SkillMasteryVO;
import com.ahuralearn.adaptiveexam.mapper.AssessmentAnswerDetailMapper;
import com.ahuralearn.adaptiveexam.mapper.AssessmentMapper;
import com.ahuralearn.adaptiveexam.mapper.QuestionMapper;
import com.ahuralearn.adaptiveexam.service.IAssessmentService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentServiceImpl
        implements IAssessmentService {


    // =========================================================
    // 临时用户 ID
    //
    // 后续接入组长 User/Auth 模块后，改为：
    //   Long userId = UserContext.getUser();
    //
    // user 表主键类型为 bigint，所以这里用 Long
    // =========================================================

    private Long getCurrentUserId() {
        Long userId = com.ahuralearn.common.utils.UserContext.getUser();
        if (userId == null) {
            throw new RuntimeException("User is not logged in");
        }
        return userId;
    }


    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private AssessmentMapper assessmentMapper;


    @Autowired
    private AssessmentAnswerDetailMapper answerDetailMapper;


    @Autowired
    private ObjectMapper objectMapper;


    // =========================================================
    // 提交考试
    // =========================================================

    @Override
    @Transactional
    public ExamReportVO submitExam(
            SubmitExamDTO dto
    ) {

        // =====================================================
        // 1. 获取 moduleId
        // =====================================================

        String moduleId = dto.getModuleId();

        if (moduleId == null
                || moduleId.isBlank()) {

            moduleId = "c_001";
        }


        // =====================================================
        // 2. 查询本次考试题目
        // =====================================================

        List<QuestionBank> questionList =
                questionMapper
                        .selectQuestionsByModule(
                                moduleId
                        );


        // =====================================================
        // 3. 防止产生 0 题、0 分的垃圾考试记录
        // =====================================================

        if (questionList == null
                || questionList.isEmpty()) {

            throw new IllegalStateException(
                    "No questions found for module: "
                            + moduleId
            );
        }


        // =====================================================
        // 4. 获取用户答案，并筛选出本次考试的真实题目
        // =====================================================

        Map<String, String> userAnswers = dto.getAnswers();
        if (userAnswers == null) userAnswers = new HashMap<>();
        Map<String, String> shortAnswers = dto.getShortAnswers();
        if (shortAnswers == null) shortAnswers = new HashMap<>();

        java.util.Set<String> examQuestionIds = new java.util.HashSet<>(userAnswers.keySet());
        examQuestionIds.addAll(shortAnswers.keySet());

        List<QuestionBank> examQuestions = new ArrayList<>();
        for (QuestionBank q : questionList) {
            if (examQuestionIds.contains(q.getId())) {
                examQuestions.add(q);
            }
        }

        int totalQuestions = examQuestions.size();
        
        if (totalQuestions == 0) {
             throw new IllegalStateException("None of the submitted questions matched the database. This usually means mock data was submitted.");
        }

        int correctCount = 0;


        // =====================================================
        // recordId 和时间字段由 MP 自动生成，此处不再手动赋值
        // =====================================================


        // =====================================================
        // 前端 Feedback 判题结果
        // =====================================================

        List<ExamReportVO.TestResult>
                testResults =
                new ArrayList<>();


        // =====================================================
        // 所有答题详情
        //
        // 先暂存在内存
        // 等 assessment_record 保存后再 INSERT
        // =====================================================

        List<AssessmentAnswerDetail>
                detailList =
                new ArrayList<>();


        // =====================================================
        // 所有错题的数据 (用于保存到 wrong_question_content)
        // =====================================================

        List<Map<String, Object>> wrongQuestionsData =
                new ArrayList<>();


        // =====================================================
        // 5. 逐题判卷
        // =====================================================

        for (QuestionBank question : examQuestions) {


            // =================================================
            // 获取用户答案
            // =================================================

            String userAnswer = null;

            if (userAnswers.containsKey(question.getId())) {
                userAnswer = userAnswers.get(question.getId());
            } else if (shortAnswers.containsKey(question.getId())) {
                userAnswer = shortAnswers.get(question.getId());
            }


            // =================================================
            // 判断答案是否正确
            //
            // 兼容全部格式组合（两边都做解析）：
            //   DB correct_answer = "second"  + 用户提交 "Multiple behavior"  → 均解析为文本再对比
            //   DB correct_answer = "文本"    + 用户提交 "second"             → 均解析为文本再对比
            //   DB correct_answer = "文本"    + 用户提交 "文本"               → 直接对比
            // =================================================

            String resolvedUserAnswer = resolveOptionAnswer(
                    userAnswer,
                    question.getOptionsJson()
            );

            String resolvedCorrectAnswer = resolveOptionAnswer(
                    question.getCorrectAnswer(),
                    question.getOptionsJson()
            );

            boolean isCorrect;

            if ("short-answer".equals(question.getType())) {
                // 对于简答题，只要用户填写了答案（非空），就暂时判为正确
                isCorrect = userAnswer != null && !userAnswer.trim().isEmpty();
            } else {
                isCorrect = resolvedUserAnswer != null
                        && resolvedCorrectAnswer != null
                        && resolvedCorrectAnswer
                        .trim()
                        .equalsIgnoreCase(
                                resolvedUserAnswer.trim()
                        );
            }


            // =================================================
            // 正确题统计
            // =================================================

            if (isCorrect) {

                correctCount++;

            } else {

                // =============================================
                // 错题：
                // 收集错题详细数据
                // =============================================

                Map<String, Object> wrongData = new HashMap<>();
                wrongData.put("id", question.getId());
                wrongData.put("type", question.getType());
                wrongData.put("text", question.getQuestionText());
                wrongData.put("topic", question.getTopic());
                wrongData.put("userAnswer", resolvedUserAnswer);
                wrongData.put("correctAnswer", resolvedCorrectAnswer);
                
                wrongQuestionsData.add(wrongData);
            }


            // =================================================
            // 保存返回给 Feedback 的简单判题结果
            // =================================================

            ExamReportVO.TestResult result =
                    new ExamReportVO.TestResult();

            result.setId(
                    question.getId()
            );

            result.setIsCorrect(
                    isCorrect
            );

            testResults.add(
                    result
            );


            // =================================================
            // 创建答题详情对象
            //
            // 此处先不 INSERT
            // =================================================

            AssessmentAnswerDetail detail =
                    new AssessmentAnswerDetail();


            // ================================================
            // id / createTime / updateTime 由 MP 自动填充
            // recordId 在 record 插入后才能获得，稍后在插入循环中赋值
            // ================================================

            detail.setQuestionId(
                    question.getId()
            );


            detail.setUserAnswer(
                    userAnswer
            );


            detail.setIsCorrect(
                    isCorrect
            );


            detailList.add(
                    detail
            );
        }


        // =====================================================
        // 6. 计算成绩
        // =====================================================

        int score =
                (correctCount * 100)
                        / totalQuestions;


        double accuracy =
                correctCount
                        * 100.0
                        / totalQuestions;


        // =====================================================
        // 7. 获取考试总时间
        // =====================================================

        int totalTime = 0;


        if (dto.getTimeStats() != null

                && dto
                .getTimeStats()
                .getTotalTimeSeconds()
                != null) {

            totalTime =
                    dto
                            .getTimeStats()
                            .getTotalTimeSeconds();
        }


        // =====================================================
        // 8. 将错题内容转换成 JSON
        //
        // 示例：
        //
        // [
        //   "Question A",
        //   "Question B"
        // ]
        // =====================================================

        // =====================================================

        String wrongQuestionContent;

        try {

            wrongQuestionContent =
                    objectMapper
                            .writeValueAsString(
                                    wrongQuestionsData
                            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to convert wrong questions to JSON",
                    e
            );
        }


        // =====================================================
        // 9. 创建考试总记录
        // =====================================================

        AssessmentRecord record =
                new AssessmentRecord();


        // id 由 MP 雪花算法自动生成，无需手动设置


        // TODO:
        //
        // 后续接入组长 User/Auth 模块后
        // 替换为真正登录用户 ID

        record.setUserId(
                getCurrentUserId()
        );


        // TODO:
        //
        // 下一阶段继续处理
        // courseId / lessonId 数据流

        record.setLessonId(
                null
        );


        record.setCourseId(
                dto.getCourseId()
        );


        record.setModuleId(
                moduleId
        );


        // =====================================================
        // 后端自动计算并保存成绩
        // =====================================================

        record.setStatus(
                2
        );

        record.setScore(
                score
        );


        record.setAccuracy(
                accuracy
        );


        record.setTotalQuestions(
                totalQuestions
        );


        record.setCorrectCount(
                correctCount
        );


        // =====================================================
        // 后端自动保存错题内容 JSON
        // =====================================================

        record.setWrongQuestionContent(
                wrongQuestionContent
        );


        record.setTimeTaken(
                totalTime
        );


        // =====================================================
        // 10. 先保存考试主记录
        //
        // createTime / updateTime 由 MetaObjectHandler 自动填充
        // id 由 MP ASSIGN_ID 雪花算法自动填充并回写到 record 对象
        // =====================================================

        assessmentMapper.insert(record);


        // =====================================================
        // 插入后从 record 对象取回 MP 生成的雪花 ID
        // =====================================================

        String recordId = record.getId();


        // =====================================================
        // 11. 再保存所有答题详情
        //
        // 此时 recordId 已确定，挨个赋值后用 answerDetailMapper 插入
        // =====================================================

        for (AssessmentAnswerDetail detail
                : detailList) {

            detail.setRecordId(recordId);

            answerDetailMapper.insert(detail);
        }


        // =====================================================
        // 12. 返回 Feedback 数据
        // =====================================================

        ExamReportVO report =
                new ExamReportVO();


        report.setAssessmentId(
                recordId
        );


        report.setScore(
                score
        );


        report.setCorrectCount(
                correctCount
        );


        report.setTotalQuestions(
                totalQuestions
        );


        report.setTimeTaken(
                totalTime
        );


        report.setAccuracyRate(
                accuracy
        );


        report.setTestResults(
                testResults
        );


        return report;
    }


    // =========================================================
    // Dashboard
    // =========================================================

    @Override
    public DashboardVO getDashboard() {

        DashboardVO dashboard =
                new DashboardVO();


        List<AssessmentRecord> records =
                assessmentMapper
                        .selectRecordsByUser(
                                getCurrentUserId()
                        );


        // =====================================================
        // 没有考试历史
        // =====================================================

        if (records == null
                || records.isEmpty()) {

            dashboard.setTotalAttempts(0);

            dashboard.setAverageScore(0.0);

            dashboard.setHighestScore(0);

            dashboard.setLatestScore(0);

            dashboard.setAccuracyRate(0.0);

            dashboard.setAverageTime(0.0);

            dashboard.setSkills(
                    new ArrayList<>()
            );

            dashboard.setRecentAssessments(
                    new ArrayList<>()
            );

            return dashboard;
        }


        // =====================================================
        // Dashboard 总体统计
        // =====================================================

        int totalAttempts =
                records.size();


        int highestScore = 0;

        double scoreSum = 0.0;

        double accuracySum = 0.0;

        double timeSum = 0.0;


        for (AssessmentRecord record
                : records) {


            if (record.getScore() != null) {

                scoreSum +=
                        record.getScore();


                highestScore =
                        Math.max(
                                highestScore,
                                record.getScore()
                        );
            }


            if (record.getAccuracy()
                    != null) {

                accuracySum +=
                        record.getAccuracy();
            }


            if (record.getTimeTaken()
                    != null) {

                timeSum +=
                        record.getTimeTaken();
            }
        }


        // =====================================================
        // records 已按 create_time DESC 排序
        // =====================================================

        int latestScore =
                records
                        .get(0)
                        .getScore()
                        == null

                        ? 0

                        : records
                        .get(0)
                        .getScore();


        dashboard.setTotalAttempts(
                totalAttempts
        );


        dashboard.setAverageScore(
                scoreSum
                        / totalAttempts
        );


        dashboard.setHighestScore(
                highestScore
        );


        dashboard.setLatestScore(
                latestScore
        );


        dashboard.setAccuracyRate(
                accuracySum
                        / totalAttempts
        );


        dashboard.setAverageTime(
                timeSum
                        / totalAttempts
        );


        // =====================================================
        // Skill Mastery — 三个固定考试维度
        //
        // 1. Accuracy  ：历次考试的平均正确率
        // 2. Speed     ：答题速度（越快分数越高）
        // 3. Problem Solving：高难度题（difficulty>=3）正确率
        // =====================================================

        List<SkillMasteryVO> skills = new ArrayList<>();


        // -------------------------------------------------
        // 1. Accuracy
        // accuracy 字段已是百分比 (0~100)，直接取均值
        // -------------------------------------------------

        Double avgAccuracy =
                assessmentMapper.selectAvgAccuracy(getCurrentUserId());

        double accuracyRate =
                (avgAccuracy != null)
                        ? Math.min(100.0, Math.max(0.0, avgAccuracy))
                        : 0.0;

        SkillMasteryVO accuracySkill = new SkillMasteryVO();
        accuracySkill.setTopic("Accuracy");
        accuracySkill.setMasteryRate(
                Math.round(accuracyRate * 10.0) / 10.0
        );
        accuracySkill.setSubtitle(
                String.format("%.1f%% average correct rate", accuracyRate)
        );
        skills.add(accuracySkill);


        // -------------------------------------------------
        // 2. Speed
        //
        // 将平均每题耗时（秒）映射到 0~100 分：
        //   ≤ 10 sec/q  → 100 分
        //   ≥ 120 sec/q → 0 分
        //   线性插值
        // -------------------------------------------------

        Double avgSecsPerQ =
                assessmentMapper.selectAvgTimePerQuestion(getCurrentUserId());

        double speedRate = 0.0;
        String speedSubtitle = "No data yet";

        if (avgSecsPerQ != null && avgSecsPerQ >= 0) {

            double secs = avgSecsPerQ;

            if (secs <= 10.0) {
                speedRate = 100.0;
            } else if (secs >= 120.0) {
                speedRate = 0.0;
            } else {
                speedRate = (120.0 - secs) / (120.0 - 10.0) * 100.0;
            }

            speedSubtitle = String.format(
                    "Avg %.1f sec per question", secs
            );
        }

        SkillMasteryVO speedSkill = new SkillMasteryVO();
        speedSkill.setTopic("Speed");
        speedSkill.setMasteryRate(
                Math.round(speedRate * 10.0) / 10.0
        );
        speedSkill.setSubtitle(speedSubtitle);
        skills.add(speedSkill);


        // -------------------------------------------------
        // 3. Problem Solving
        //
        // difficulty >= 3 的题目中答对比例
        // 若暂无高难度题答题记录，默认 0
        // -------------------------------------------------

        Double problemSolvingRate =
                assessmentMapper.selectProblemSolvingRate(getCurrentUserId());

        double psRate =
                (problemSolvingRate != null)
                        ? Math.min(100.0, Math.max(0.0, problemSolvingRate))
                        : 0.0;

        SkillMasteryVO psSkill = new SkillMasteryVO();
        psSkill.setTopic("Problem Solving");
        psSkill.setMasteryRate(
                Math.round(psRate * 10.0) / 10.0
        );
        psSkill.setSubtitle(
                String.format("%.1f%% on difficulty ≥ 3 questions", psRate)
        );
        skills.add(psSkill);


        dashboard.setSkills(skills);


        // =====================================================
        // Recent Assessments
        //
        // 最近 5 次考试
        // =====================================================

        List<RecentAssessmentVO>
                recentAssessments =
                new ArrayList<>();


        int recentCount =
                Math.min(
                        5,
                        records.size()
                );


        for (int i = 0;
             i < recentCount;
             i++) {


            AssessmentRecord record =
                    records.get(i);


            RecentAssessmentVO recent =
                    new RecentAssessmentVO();


            recent.setRecordId(
                    record.getId()
            );


            recent.setModuleId(
                    record.getModuleId()
            );


            recent.setScore(
                    record.getScore()
            );


            recent.setAccuracy(
                    record.getAccuracy()
            );


            recent.setTotalQuestions(
                    record.getTotalQuestions()
            );


            recent.setCorrectCount(
                    record.getCorrectCount()
            );


            recent.setTimeTaken(
                    record.getTimeTaken()
            );


            // RecentAssessmentVO 当前字段仍叫 createdAt
            // 所以这里只做映射，不需要修改前端

            recent.setCreatedAt(
                    record.getCreateTime()
            );


            recentAssessments.add(
                    recent
            );
        }


        dashboard.setRecentAssessments(
                recentAssessments
        );


        return dashboard;
    }


    // =========================================================
    // Feedback / Question Review
    // =========================================================

    @Override
    public ExamReportVO getReport(
            String recordId
    ) {


        AssessmentRecord record =
                assessmentMapper
                        .selectRecordById(
                                recordId
                        );


        if (record == null) {

            return null;
        }


        List<AssessmentDetailVO> details =
                assessmentMapper
                        .selectAssessmentDetails(
                                recordId
                        );


        if (details == null) {

            details =
                    new ArrayList<>();
        }


        ExamReportVO report =
                new ExamReportVO();


        report.setAssessmentId(
                record.getId()
        );


        report.setScore(
                record.getScore()
        );


        report.setTimeTaken(
                record.getTimeTaken()
        );


        report.setDetails(
                details
        );


        int correctCount =
                (int) details
                        .stream()
                        .filter(
                                detail ->
                                        Boolean.TRUE.equals(
                                                detail.getIsCorrect()
                                        )
                        )
                        .count();


        report.setCorrectCount(
                correctCount
        );


        report.setTotalQuestions(
                details.size()
        );


        double accuracyRate =
                details.isEmpty()
                        ? 0.0
                        : correctCount * 100.0 / details.size();


        report.setAccuracyRate(
                accuracyRate
        );


        return report;
    }


    // =========================================================
    // 私有辅助：将前端提交的 option id 解析为真实 option 文本
    //
    // 支持格式：
    //   "first"/"second"/"third"/"fourth" → options[0..3]
    //   "A"/"B"/"C"/"D"                  → options[0..3]
    //   "0"/"1"/"2"/"3"                  → options[0..3]
    //   已是文本（如 "Multiple behavior") → 原样返回
    // =========================================================

    private String resolveOptionAnswer(String userAnswer, String optionsJson) {

        if (userAnswer == null || optionsJson == null || optionsJson.isBlank()) {
            return userAnswer;
        }

        try {
            String[] options = objectMapper.readValue(optionsJson, String[].class);

            if (options == null || options.length == 0) {
                return userAnswer;
            }

            Map<String, Integer> wordIndex = new HashMap<>();
            wordIndex.put("first",  0);
            wordIndex.put("second", 1);
            wordIndex.put("third",  2);
            wordIndex.put("fourth", 3);

            String lower = userAnswer.trim().toLowerCase();

            // 1. 序数词
            Integer idx = wordIndex.get(lower);
            if (idx != null && idx < options.length) {
                return options[idx];
            }

            // 2. 字母 A/B/C/D
            if (lower.length() == 1 && Character.isLetter(lower.charAt(0))) {
                int letterIdx = Character.toUpperCase(lower.charAt(0)) - 'A';
                if (letterIdx >= 0 && letterIdx < options.length) {
                    return options[letterIdx];
                }
            }

            // 3. 数字索引
            try {
                int numIdx = Integer.parseInt(lower);
                if (numIdx >= 0 && numIdx < options.length) {
                    return options[numIdx];
                }
            } catch (NumberFormatException ignored) {
                // 继续
            }

        } catch (Exception e) {
            // JSON 解析失败，原样返回
        }

        return userAnswer;
    }

    @Override
    public java.util.List<AssessmentRecord> getHistory(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssessmentRecord> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("create_time");
        return assessmentMapper.selectList(qw);
    }
}
